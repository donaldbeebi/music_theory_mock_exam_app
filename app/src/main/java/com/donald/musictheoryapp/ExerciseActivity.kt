package com.donald.musictheoryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.auth0.jwt.interfaces.DecodedJWT
import com.donald.musictheoryapp.composables.StandardScreen
import com.donald.musictheoryapp.composables.activitycomposables.exercise.*
import com.donald.musictheoryapp.composables.activitycomposables.exercise.input.TimerState
import com.donald.musictheoryapp.composables.decoration.TopShadow
import com.donald.musictheoryapp.composables.general.CircularLoadingIndicator
import com.donald.musictheoryapp.composables.general.StandardAlertDialog
import com.donald.musictheoryapp.composables.general.StandardTextButton
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.exerciseTypography
import com.donald.musictheoryapp.composables.theme.moreColors
import com.donald.musictheoryapp.pagedexercise.Page
import com.donald.musictheoryapp.pagedexercise.PagedExercise
import com.donald.musictheoryapp.question.ChildQuestion
import com.donald.musictheoryapp.question.Description
import com.donald.musictheoryapp.question.Exercise
import com.donald.musictheoryapp.question.MultipleChoiceQuestion
import com.donald.musictheoryapp.util.*
import com.donald.musictheoryapp.util.Time.Companion.min
import com.donald.musictheoryapp.util.Time.Companion.ms
import com.donald.musictheoryapp.util.Time.Companion.sec
import com.donald.musictheoryapp.util.exercise.ExerciseTimer
import com.donald.musictheoryapp.util.exercise.ExerciseTimerListener
import com.donald.musictheoryapp.util.practiceoptions.PracticeOptions
import org.json.JSONObject
import java.util.concurrent.Future

class ExerciseActivity : AppCompatActivity(), ImageProvider, ExerciseTimerListener {
    // TODO: BACK PRESSED --> LOSE FOCUS
    private lateinit var activityStateDelegate: MutableState<ActivityState>
    private var currentActivityState: ActivityState
        get() = activityStateDelegate.value
        set(value) { activityStateDelegate.value = value }
    /*private val exerciseInfo: ExerciseInfo?
        get() = (activityState as? ActivityState.ViewingExercise)?.exerciseInfo*/
    private var currentDialog by mutableStateOf<ExerciseDialog?>(null)
    private lateinit var profile: Profile

    private fun onExerciseDownloaded(exercise: Exercise) {
        require(currentActivityState is ActivityState.Downloading)
        val pagedExercise = PagedExercise(exercise, { getString(R.string.question_string, it) })
        val initialPage = pagedExercise[0]
        val exerciseState: ExerciseState = ExerciseState.RegularMode(
            ExerciseTimer(this, lifecycleScope).also { it.start() }
        )

        val newActivityState = ActivityState.ViewingExercise(
            exercise = exercise,
            pagedExercise = pagedExercise,
            exerciseState = exerciseState,
            downloadFutures = ArrayList(),
            imageStatuses = SnapshotStateMap<String, ImageStatus>().apply {
                pagedExercise.imagesToLoad.forEach { put(it, ImageStatus.Loading) }
            },
            currentPageStateDelegate = mutableStateOf(
                PageState.Loading(
                    sectionString = initialPage.sectionString,
                    questionString = initialPage.questionString,
                    pageIndex = 0,
                    pageCount = pagedExercise.pageCount,
                    pageMode = exerciseState.toPageMode(exercise.timeRemaining),
                    onNavigate = { onNavigate(it) },
                    onPause = { pauseTimer() }
                )
            )
        )
        currentActivityState = newActivityState
        with (newActivityState) { loadImagesAsync() }
    }

    private fun onInsufficientPoints() {
        currentActivityState = ActivityState.InsufficientPoints
    }

    private fun pauseTimer(): Unit = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        require(exerciseState is ExerciseState.RegularMode)
        this.exerciseState.timer.pause()
    }

    override fun onTimerPause(/*millisElapsedSinceLastUpdate: Long*/): Unit = runMain {
        Log.d("ExerciseActivity", "onTimerPause called, millisSinceTick")
        with(currentActivityState) {
            require(this is ActivityState.ViewingExercise)
            val timeRemaining = (exercise.timeRemaining.millis/* - millisElapsedSinceLastUpdate*/).ms
            //exercise.timeRemaining = timeRemaining
            val exerciseState = this.exerciseState as ExerciseState.RegularMode
            val currentPage = pagedExercise[currentPageStateDelegate.value.pageIndex]
            val previousPageStatus = currentPageStateDelegate.value
            currentPageStateDelegate.value = PageState.Paused(
                sectionString = currentPage.sectionString,
                questionString = currentPage.questionString,
                pageIndex = currentPageStateDelegate.value.pageIndex,
                pageCount = currentPageStateDelegate.value.pageCount,
                pageMode = PageMode.RegularMode(timeRemaining),
                onResume = {
                    // TODO: THIS SEEMS PRONE TO BUGS
                    currentPageStateDelegate.value = previousPageStatus
                    exerciseState.timer.start()
                }
            )
        }
    }

    private fun onStopReadingExercise() {
        currentDialog = null
        finish()
    }

    private fun onEndExercise(): Unit = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        exercise.timeRemaining = 0.sec
        runDiskIO { saveExercise(exercise) }
        downloadFutures.forEach { it.cancel(false) }
        currentDialog = null
        finish()
    }

    private fun onSaveExercise(): Unit = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        exercise.savedPageIndex = currentPageStateDelegate.value.pageIndex
        runDiskIO { saveExercise(exercise) }
        downloadFutures.forEach { it.cancel(false) }
        currentDialog = null
        finish()
    }

    private fun onAttemptExit(): Unit = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        when (exerciseState) {
            ExerciseState.ReadMode -> onStopReadingExercise()
            is ExerciseState.RegularMode -> showConfirmExitDialog()
        }
    }

    private fun showConfirmExitDialog() {
        require(
            (currentActivityState as ActivityState.ViewingExercise)
                .exerciseState is ExerciseState.RegularMode
        )
        currentDialog = ExerciseDialog.ConfirmExit(
            onContinue = { currentDialog = null },
            onEnd = { onEndExercise() },
            onSave = { onSaveExercise() },
            onDismiss = { currentDialog = null }
        )
    }

    private fun showOutOfTimeDialog(): Unit = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        require(exerciseState is ExerciseState.RegularMode)
        exercise.timeRemaining = 0.sec
        runDiskIO { saveExercise(exercise) }
        downloadFutures.forEach { it.cancel(false) }
        currentDialog = ExerciseDialog.OutOfTime(
            onAcknowledge = { currentDialog = null; finish() },
            onDismiss = { Unit }
        )
    }

    private fun onNavigate(pageIndex: Int): Unit = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        // TODO: THERE ISN'T SUPPOSED TO BE AN EXCEPTION THROWN HERE, DEBUG THIS SHIT, ANNOYING AF
        try {
            require(pageIndex != currentPageStateDelegate.value.pageIndex)
            if (pageIndex == pagedExercise.pageCount) return onAttemptExit()
            updatePageStatus(pageIndex)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onTimerTick(millisTicked: Long): Unit = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        require(exerciseState is ExerciseState.RegularMode)
        exercise.timeRemaining = (exercise.timeRemaining.millis - millisTicked).coerceAtLeast(0L).ms
        if (exercise.timeRemaining == 0.ms) showOutOfTimeDialog()
        else {
            currentPageStateDelegate.value = when (val pageState = currentPageStateDelegate.value) {
                is PageState.Loading -> newLoadingPageState(pageState.pageIndex)
                is PageState.Error -> newLoadingPageState(pageState.pageIndex)
                is PageState.Ready -> newReadyPageState(pageState.pageIndex)
                is PageState.Peeking -> newPeekingPageState(pageState.pageIndex, pageState.pageIndexToReturn)
                is PageState.Paused -> throw IllegalStateException("onTick called when the exercise is paused")
            }
        }
    }

    private fun updatePageStatus(pageIndex: Int) = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        val page = pagedExercise[pageIndex]
        val newPageStatus = when {
            pageIsLoading(page.images, imageStatuses) -> newLoadingPageState(pageIndex)
            pageIsError(page.images, imageStatuses) -> newErrorPageState(pageIndex)
            else -> newReadyPageState(pageIndex)
        }
        currentPageStateDelegate.value = newPageStatus
    }

    private fun onPeek(pageToPeekIndex: Int, pageToReturnIndex: Int) = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        /*val pagePeeked = pagedExercise[pageToPeekIndex]
        val newPageState = PageState.Peeking(
            sectionString = pagePeeked.sectionString,
            questionString = pagePeeked.questionString,
            pageIndex = pageToPeekIndex,
            pageCount = pageCount,
            pageMode = exerciseState.toPageMode(),
            descriptions = pagePeeked.descriptions,
            onUnpeek = { onUnpeek(pageToReturnIndex) },
            onPause = { onPauseExercise() }
        )*/
        currentPageStateDelegate.value = newPeekingPageState(pageToPeekIndex, pageToReturnIndex)
    }

    private fun onUnpeek(pageToReturnIndex: Int) = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        currentPageStateDelegate.value = newReadyPageState(pageToReturnIndex)
    }

    private fun onReload(pageIndex: Int) = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        val page = pagedExercise[pageIndex]
        page.images.forEach { imageName ->
            if (imageStatuses.getValue(imageName) == ImageStatus.Error) {
                loadImageAsync(imageName)
                Log.d("ExerciseActivity", "Reloading image $imageName")
            }
        }
        updatePageStatus(pageIndex)
    }

    private fun newPeekingPageState(pageToPeekIndex: Int, pageIndexToReturn: Int): PageState.Peeking = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        val pagePeeked = pagedExercise[pageToPeekIndex]
        return PageState.Peeking(
            sectionString = pagePeeked.sectionString,
            questionString = pagePeeked.questionString,
            pageIndex = pageToPeekIndex,
            pageCount = pagedExercise.pageCount,
            pageMode = exerciseState.toPageMode(exercise.timeRemaining),
            descriptions = if (pageIsLoading(pagePeeked.images, imageStatuses)) null else pagePeeked.descriptions,
            pageIndexToReturn = pageIndexToReturn,
            onUnpeek = { onUnpeek(it) },
            onPause = { pauseTimer() }
        )
    }

    private fun newLoadingPageState(pageIndex: Int): PageState.Loading = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        val page = pagedExercise[pageIndex]
        return PageState.Loading(
            sectionString = page.sectionString,
            questionString = page.questionString,
            pageIndex = pageIndex,
            pageCount = pagedExercise.pageCount,
            pageMode = exerciseState.toPageMode(exercise.timeRemaining),
            onNavigate = { onNavigate(it) },
            onPause = { pauseTimer() }
        )
    }

    private fun newErrorPageState(pageIndex: Int): PageState.Error = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        val page = pagedExercise[pageIndex]
        return PageState.Error(
            sectionString = page.sectionString,
            questionString = page.questionString,
            pageIndex = pageIndex,
            pageCount = pagedExercise.pageCount,
            pageMode = exerciseState.toPageMode(exercise.timeRemaining),
            onNavigate = { onNavigate(it) },
            onReloadPage = { onReload(pageIndex) },
            onPause = { pauseTimer() }
        )
    }

    private fun newReadyPageState(pageIndex: Int): PageState.Ready = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        val page = pagedExercise[pageIndex]
        return PageState.Ready(
            sectionString = page.sectionString,
            questionString = page.questionString,
            pageIndex = pageIndex,
            pageCount = pagedExercise.pageCount,
            pageMode = exerciseState.toPageMode(exercise.timeRemaining),
            descriptions = page.descriptions,
            question = page.question,
            inputPanelExpanded = inputPanelExpanded,
            onTogglePanelExpand = {
                inputPanelExpanded = !inputPanelExpanded
                updatePageStatus(pageIndex)
            },
            onNavigate = { onNavigate(it) },
            onPeek = page.pageToPeekIndex?.let { { onPeek(it, currentPageStateDelegate.value.pageIndex) } },
            onPause = { pauseTimer() }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profile = CurrentProfile ?: throw IllegalStateException("Profile not set")

        val action = intent.getStringExtra("action") ?: throw IllegalStateException("No action provided")
        when (action) {
            "redo" -> {
                activityStateDelegate = mutableStateOf(ActivityState.Loading)
                val exerciseData = intent.getParcelableExtra<ExerciseData>("exercise_data") ?: throw IllegalStateException("No exercise data provided")
                if (exerciseData.timeRemaining == 0.sec) {
                    TODO()
                } else {
                    runDiskIO {
                        val exercise = this.retrieveExerciseOrNull(exerciseData)
                        if (exercise == null) {
                            runMain {
                                displayToastForFailedExerciseRetrieval(this)
                                Log.d("ExerciseActivity", "Error while retrieving for exercise $exerciseData")
                            }
                            return@runDiskIO
                        }
                        val pagedExercise = PagedExercise(exercise, { getString(R.string.question_string, it) })
                        runMain {
                            val imageStatuses = SnapshotStateMap<String, ImageStatus>().apply {
                                pagedExercise.imagesToLoad.forEach { put(it, ImageStatus.Loading) }
                            }
                            val exerciseState = ExerciseState.RegularMode(ExerciseTimer(this, lifecycleScope))
                            val newActivityState = ActivityState.ViewingExercise(
                                exercise = exercise,
                                pagedExercise = PagedExercise(exercise, { getString(R.string.question_string, it) }),
                                exerciseState = exerciseState,
                                downloadFutures = ArrayList(),
                                imageStatuses = imageStatuses,
                                currentPageStateDelegate = mutableStateOf(
                                    getInitialPageState(
                                        pagedExercise = pagedExercise,
                                        initialPageIndex = 0,
                                        imageStatuses = imageStatuses,
                                        pageMode = exerciseState.toPageMode(exercise.timeRemaining)
                                    )
                                )
                            )
                            currentActivityState = newActivityState
                            with(newActivityState) { loadImagesAsync() }
                        }
                    }
                }
            }

            "download" -> runNetworkIO {
                activityStateDelegate = mutableStateOf(ActivityState.Downloading)
                val accessToken = TokenManager.tryGetAccessToken(this).otherwise { return@runNetworkIO handleTokenError(it) }
                val mode = intent.getStringExtra("mode") ?: throw IllegalStateException("No mode provided")
                when (mode) {
                    "practice" -> {
                        val options: PracticeOptions = intent.getParcelableExtra("options")
                            ?: throw IllegalStateException("No options provided")
                        //val optionsJson = JSONObject(intent.getStringExtra("options_json") ?: throw IllegalStateException())
                        downloadPractice(accessToken, options)
                    }
                    "test" -> {
                        downloadTest(accessToken)
                    }
                    else -> {
                        throw IllegalStateException("Mode $mode not recognized")
                    }
                }
            }

            "resume" -> {
                activityStateDelegate = mutableStateOf(ActivityState.Loading)
                val exerciseData = intent.getParcelableExtra<ExerciseData>("exercise_data") ?: throw IllegalStateException("No exercise data provided")
                if (exerciseData.timeRemaining == 0.sec) {
                    TODO()
                } else {
                    runDiskIO {
                        val exercise = this.retrieveExerciseOrNull(exerciseData)
                        if (exercise == null) {
                            runMain {
                                displayToastForFailedExerciseRetrieval(this)
                                Log.d("ExerciseActivity", "Error while retrieving for exercise $exerciseData")
                            }
                            return@runDiskIO
                        }
                        val pagedExercise = PagedExercise(exercise, { getString(R.string.question_string, it) })
                        runMain {
                            val imageStatuses = SnapshotStateMap<String, ImageStatus>().apply {
                                pagedExercise.imagesToLoad.forEach { put(it, ImageStatus.Loading) }
                            }
                            val exerciseState = ExerciseState.RegularMode(
                                timer = ExerciseTimer(this, lifecycleScope)
                            )
                            val newActivityState = ActivityState.ViewingExercise(
                                exercise = exercise,
                                pagedExercise = PagedExercise(exercise, { getString(R.string.question_string, it) }),
                                exerciseState = exerciseState,
                                downloadFutures = ArrayList(),
                                imageStatuses = imageStatuses,
                                currentPageStateDelegate = mutableStateOf(
                                    getInitialPageState(
                                        pagedExercise = pagedExercise,
                                        initialPageIndex = exercise.savedPageIndex,
                                        imageStatuses = imageStatuses,
                                        pageMode = exerciseState.toPageMode(exercise.timeRemaining)
                                    )
                                )
                            )
                            currentActivityState = newActivityState
                            with(newActivityState) { loadImagesAsync() }
                        }
                    }
                }
            }

            "read" -> {
                activityStateDelegate = mutableStateOf(ActivityState.Loading)
                val exerciseData = intent.getParcelableExtra<ExerciseData>("exercise_data") ?: throw IllegalStateException("No exercise data provided!")
                val sectionGroupIndex = intent.getIntExtra("section_group_index", -1).also { require(it != -1) }
                val sectionIndex = intent.getIntExtra("section_index", -1).also { require(it != -1) }
                val groupIndex = intent.getIntExtra("group_index", -1).also { require(it != -1) }
                runDiskIO {
                    val exercise = retrieveExercise(exerciseData)
                    val targetQuestion = exercise
                        .sectionGroups[sectionGroupIndex]
                        .sections[sectionIndex]
                        .questionGroups[groupIndex]
                        .parentQuestions[0]
                        .childQuestions[0]
                    val pagedExercise = PagedExercise(exercise, { getString(R.string.question_string, it) })
                    runMain {
                        val imageStatuses = SnapshotStateMap<String, ImageStatus>().apply {
                            pagedExercise.imagesToLoad.forEach { put(it, ImageStatus.Loading) }
                        }
                        val exerciseState = ExerciseState.ReadMode
                        val newActivityState = ActivityState.ViewingExercise(
                            exercise = exercise,
                            pagedExercise = PagedExercise(exercise, { getString(R.string.question_string, it) }),
                            exerciseState = exerciseState,
                            downloadFutures = ArrayList(),
                            imageStatuses = imageStatuses,
                            currentPageStateDelegate = mutableStateOf(
                                getInitialPageState(
                                    pagedExercise = pagedExercise,
                                    initialPageIndex = pagedExercise.pageIndexOf(targetQuestion),
                                    imageStatuses = imageStatuses,
                                    pageMode = exerciseState.toPageMode(exercise.timeRemaining)
                                )
                            )
                        )
                        currentActivityState = newActivityState
                        with(newActivityState) { loadImagesAsync() }
                    }
                }
            }

            else -> throw IllegalStateException()
        }

        setContent {
            CustomTheme {
                when (val activityState = this.currentActivityState) {
                    ActivityState.Downloading -> DownloadScreen()
                    ActivityState.Loading -> { Text("Loading") }
                    ActivityState.InsufficientPoints -> InsufficientPointsScreen()
                    is ActivityState.ViewingExercise -> ExerciseScreen(
                        pageState = activityState.currentPageStateDelegate.value,
                        dialog = currentDialog
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        when (currentActivityState) {
            is ActivityState.ViewingExercise -> {
                 onAttemptExit()
            }
            else -> finish()
        }
    }

    private fun downloadExercise(
        accessToken: DecodedJWT,
        bodyJson: JSONObject,
        cost: Int
    ) {
        require(profile.points >= cost) { "Insufficient points" }
        // 1. download the exercise
        val exerciseJson = downloadExerciseJson(bodyJson, accessToken) ?: run {
            runMain { this.displayToast(R.string.toast_unable_to_get_exercise) }
            finish()
            return
        }

        // 2. save the exercise
        runDiskIO {
            val exercise = storeNewExerciseJson(exerciseJson) ?: run {
                runMain { this.displayToast(R.string.toast_unable_to_get_exercise) }
                finish()
                return@runDiskIO
            }
            runMain {
                onExerciseDownloaded(exercise)
                CurrentProfile = profile.copy(points = profile.points - cost)
            }
        }
    }

    private fun downloadTest(
        accessToken: DecodedJWT
    ) {
        runNetworkIO {
            if (profile.points < TEST_COST && false) { // TODO: POINTS DETECTION DISABLED
                runMain { onInsufficientPoints() }
            } else {
                val bodyJson = JSONObject().apply {
                    put("mode", "test")
                    put("lang_pref", "en")
                }
                downloadExercise(accessToken, bodyJson, TEST_COST)
            }
        }
    }

    private fun downloadPractice(
        accessToken: DecodedJWT,
        //optionsJson: JSONObject,
        options: PracticeOptions,
    ) {
        runNetworkIO {
            val profile = CurrentProfile ?: throw IllegalStateException("Profile is null")
            if (profile.points < options.countCost(PRACTICE_COST) && false) { // TODO: POINTS DETECTION DISABLED
                runMain { onInsufficientPoints(); Log.d("ExerciseActivity", "You have ${profile.points} points but it requires ${options.countCost(PRACTICE_COST)} points") }
            } else {
                val bodyJson = JSONObject().apply {
                    put("mode", "practice")
                    put("lang_pref", "en")
                    //put("options", optionsJson)
                    put("options", options.toJson())
                }
                downloadExercise(accessToken, bodyJson, options.countCost(PRACTICE_COST))
            }
        }
    }

    private fun handleTokenError(tokenError: TokenError) = runMain {
        when (tokenError) {
            TokenError.BadResponse -> this.displayToast(R.string.toast_bad_response_from_server)
            TokenError.NoInternet -> this.displayToast(R.string.toast_no_internet)
            TokenError.Timeout -> this.displayToast(R.string.toast_timeout)
        }
    }

    // this function runs on the main thread, hopefully this eliminates any race conditions
    private fun ActivityState.ViewingExercise.updatePageToSuccessfulImageLoad(imageName: String) {
        val currentImages = pagedExercise[currentPageStateDelegate.value.pageIndex].images
        if (imageName !in currentImages) return
        // only update when the page has no more images to load
        currentPageStateDelegate.value = when {
            pageIsLoading(currentImages, imageStatuses) -> return
            pageIsError(currentImages, imageStatuses) -> newErrorPageState(currentPageStateDelegate.value.pageIndex)
            else -> newReadyPageState(currentPageStateDelegate.value.pageIndex)
        }
    }

    private fun ActivityState.ViewingExercise.updatePageToFailedImageLoad(imageName: String) {
        val currentImages = pagedExercise[currentPageStateDelegate.value.pageIndex].images
        if (imageName !in currentImages) return
        currentPageStateDelegate.value = when {
            pageIsLoading(currentImages, imageStatuses) -> return
            pageIsError(currentImages, imageStatuses) -> newErrorPageState(currentPageStateDelegate.value.pageIndex)
            else -> throw IllegalStateException("A ready/peeking page is shown before an image failed to load.")
        }
    }

    private fun ActivityState.ViewingExercise.notifyImageSuccessfullyLoaded(imageName: String, painter: Painter) {
        require(imageName in imageStatuses) { "Image name $imageName is not found" }
        imageStatuses[imageName] = ImageStatus.Successful(painter)
        updatePageToSuccessfulImageLoad(imageName)
    }

    private fun ActivityState.ViewingExercise.notifyImageFailToLoad(imageName: String) {
        require(imageName in imageStatuses) { "Image name $imageName is not found" }
        imageStatuses[imageName] = ImageStatus.Error
        updatePageToFailedImageLoad(imageName)
    }

    private fun ActivityState.ViewingExercise.loadImagesAsync() {
        pagedExercise.imagesToLoad.forEach { imageName ->
            loadImageAsync(imageName)
        }
    }

    private fun ActivityState.ViewingExercise.loadImageAsync(imageName: String) {
        imageStatuses[imageName] = ImageStatus.Loading
        if (imageExists(imageName)) runDiskIO {
            loadImageFromDisk(imageName)
        }
        else runNetworkIO {
            val accessToken = TokenManager.tryGetAccessToken(this@ExerciseActivity).otherwise { return@runNetworkIO displayToastForTokenError(it) }
            downloadAndSaveImage(imageName, accessToken)
        }
    }

    private fun ActivityState.ViewingExercise.loadImageFromDisk(imageName: String) {
        val painter = BitmapPainter(retrieveImage(imageName))
        runMain { notifyImageSuccessfullyLoaded(imageName, painter) }
    }

    private fun ActivityState.ViewingExercise.downloadAndSaveImage(imageName: String, token: DecodedJWT) {
        val future = runNetworkIO downloadImageBlock@{
            val bitmap = downloadImage(imageName, token) ?: return@downloadImageBlock this.notifyImageFailToLoad(imageName)
            runDiskIO {
                saveImage(imageName, bitmap).also { if (it == false) throw IllegalStateException("Image $imageName unable to be saved to the disk") }
                runMain { notifyImageSuccessfullyLoaded(imageName, BitmapPainter(bitmap.asImageBitmap())) }
            }
        }
        downloadFutures.add(future)
    }

    private fun displayToastForTokenError(tokenError: TokenError) {
        Log.d("ExerciseActivity", "Token error detected: ${tokenError.name}")
        runMain {
            when (tokenError) {
                TokenError.BadResponse -> this.displayToast(R.string.toast_bad_response_from_server)
                TokenError.NoInternet -> this.displayToast(R.string.toast_no_internet)
                TokenError.Timeout -> this.displayToast(R.string.toast_timeout)
            }
        }
    }

    override fun getImage(imageName: String): Painter {
        val activityState = currentActivityState
        require(activityState is ActivityState.ViewingExercise)
        return (activityState.imageStatuses.getValue(imageName) as? ImageStatus.Successful)?.painter ?: throw IllegalStateException("No image found for $imageName")
    }

    private fun getInitialPageState(
        pagedExercise: PagedExercise,
        initialPageIndex: Int,
        imageStatuses: Map<String, ImageStatus>,
        pageMode: PageMode
    ): PageState {
        val initialInputPanelExpand = true
        val initialPage = pagedExercise[initialPageIndex]
        val pageCount = pagedExercise.pageCount
        return when {
            pageIsLoading(initialPage.images, imageStatuses) -> PageState.Loading(
                sectionString = initialPage.sectionString,
                questionString = initialPage.questionString,
                pageIndex = initialPageIndex,
                pageCount = pageCount,
                pageMode = pageMode,
                onNavigate = { onNavigate(it) },
                onPause = { pauseTimer() }
            )
            pageIsError(initialPage.images, imageStatuses) -> PageState.Error(
                sectionString = initialPage.sectionString,
                questionString = initialPage.questionString,
                pageIndex = initialPageIndex,
                pageCount = pageCount,
                pageMode = pageMode,
                onNavigate = { onNavigate(it) },
                onReloadPage = { onReload(initialPageIndex) },
                onPause = { pauseTimer() }
            )
            else -> PageState.Ready(
                sectionString = initialPage.sectionString,
                questionString = initialPage.questionString,
                pageIndex = initialPageIndex,
                pageCount = pageCount,
                pageMode = pageMode,
                descriptions = initialPage.descriptions,
                question = initialPage.question,
                inputPanelExpanded = initialInputPanelExpand,
                onTogglePanelExpand = { toggleInputPanelExpand() },
                onNavigate = { onNavigate(it) },
                onPeek = initialPage.pageToPeekIndex?.let { { onPeek(it, initialPageIndex) } },
                onPause = { pauseTimer() }
            ).also { Log.d("ExerciseActivity", "initPage is ready") }
        }
    }

    private fun toggleInputPanelExpand(): Unit = with(currentActivityState) {
        require(this is ActivityState.ViewingExercise)
        inputPanelExpanded = !inputPanelExpanded
        updatePageStatus(currentPageStateDelegate.value.pageIndex)
    }

    private sealed class ActivityState {
        object Downloading : ActivityState()
        object Loading : ActivityState()
        object InsufficientPoints: ActivityState()
        class ViewingExercise(
            val exercise: Exercise,
            val pagedExercise: PagedExercise,
            val exerciseState: ExerciseState,
            val downloadFutures: ArrayList<Future<*>>,
            val imageStatuses: MutableMap<String, ImageStatus>,
            val currentPageStateDelegate: MutableState<PageState>,
            var inputPanelExpanded: Boolean = true
        ) : ActivityState()
    }
}

@Preview
@Composable
private fun ScreenPreview() = CustomTheme {
    val mockImage = painterResource(R.drawable.test_transparent_image)
    val imageProvider = ImageProvider { mockImage }
    with(imageProvider) {
        ExerciseScreen(
            pageState = PageState.Ready(
                sectionString = MockPage.sectionString,
                questionString = MockPage.questionString,
                pageIndex = 2,
                pageCount = 10,
                pageMode = PageMode.RegularMode(timeRemaining = 72.min),
                descriptions = MockPage.descriptions,
                question = MockPage.question,
                inputPanelExpanded = true,
                onTogglePanelExpand = { Unit },
                onNavigate = { Unit },
                onPeek = { Unit },
                onPause = { Unit }
            ),
            dialog = null
        )
    }
}

/*StandardScreen(
        currentActivityTitle = "Exercise",
        onBackPressed = { focusManager.clearFocus() }
    ) {*/

@Composable
private fun DownloadScreen() = Box(modifier = Modifier.fillMaxSize()) {
    CircularLoadingIndicator(
        text = stringResource(R.string.downloading_exercise),
        modifier = Modifier.align(Alignment.Center)
    )
}

@Composable
private fun InsufficientPointsScreen() = Box(modifier = Modifier.fillMaxSize()){
    Text(
        text = stringResource(R.string.toast_insufficient_points),
        style = MaterialTheme.exerciseTypography.insufficientPoints,
        color = MaterialTheme.colors.onSurface,
        modifier = Modifier.align(Alignment.Center).padding(32.dp)
    )
}

@Composable
private fun ImageProvider.ExerciseScreen(
    pageState: PageState,
    dialog: ExerciseDialog?
) {
    val focusManager = LocalFocusManager.current

    StandardScreen(
        backgroundColor = MaterialTheme.colors.surface,
        focusContent = { FocusContent(dialog = dialog) }
    ) {
        Column {
            Box(modifier = Modifier.weight(1F)) {
                when(pageState) {
                    is PageState.Loading -> PageLoadingBody()
                    is PageState.Error -> PageErrorBody(pageState.onReloadPage)
                    is PageState.Ready -> key(pageState.pageIndex) {
                        PageReadyBody(
                            pageState = pageState,
                            focusManager = focusManager
                        )
                    }
                    is PageState.Peeking -> key(pageState.pageIndex) {
                        PagePeekingBody(pageState = pageState)
                    }
                    is PageState.Paused -> PagePausedBody()
                }
                TopShadow(
                    modifier = Modifier
                        .height(4.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }

            BottomBar(
                sectionString = pageState.sectionString,
                questionString = pageState.questionString,
                timerState = pageState.timerState(),
                navigationState = pageState.navigationState(),
                peekState = pageState.peekState()
            )
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularLoadingIndicator(
        text = stringResource(R.string.downloading_image),
        modifier = modifier
    )
}

@Composable
private fun PageLoadingBody() = Box(modifier = Modifier.fillMaxSize()) {
    LoadingIndicator(Modifier.align(Alignment.Center))
}

@Composable
private fun PageErrorBody(onReloadPage: () -> Unit) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxSize()
) {
    Text(
        text = stringResource(R.string.exercise_error_page),
        style = MaterialTheme.exerciseTypography.imageLoadError,
        color = MaterialTheme.colors.onSurface,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    StandardTextButton(
        text = stringResource(R.string.exercise_reload_page_button),
        onClick = onReloadPage
    )
}

@Composable
private fun ImageProvider.PageReadyBody(
    pageState: PageState.Ready,
    focusManager: FocusManager,
) = Column {
    DescriptionPanel(
        descriptions = pageState.descriptions,
        modifier = Modifier
            .weight(1F)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
    )
    val question = pageState.question
    if (question != null) InputPanel(
        question = question,
        readMode = pageState.readMode(),
        open = pageState.inputPanelExpanded,
        onPanelHandleClick = pageState.onTogglePanelExpand,
        focusManager = focusManager,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
private fun ImageProvider.PagePeekingBody(
    pageState: PageState.Peeking,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier.fillMaxSize()
) {
    val descriptions = pageState.descriptions
    if (descriptions == null) Box(modifier = Modifier.fillMaxSize()) {
        LoadingIndicator(Modifier.align(Alignment.Center))
    } else DescriptionPanel(
        descriptions = descriptions,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .align(Alignment.Center)
    )
}

@Composable
private fun PagePausedBody(
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.moreColors.disabled)
) {
    Text(
        text = stringResource(R.string.paused),
        style = MaterialTheme.exerciseTypography.paused,
        color = MaterialTheme.moreColors.onDisabled,
        modifier = Modifier.align(Alignment.Center)
    )
}

@Composable
private fun FocusContent(dialog: ExerciseDialog?) {
    when (dialog) {
        is ExerciseDialog.ConfirmExit -> StandardAlertDialog(
            title = stringResource(R.string.exit_exercise_dialog_title),
            description = stringResource(R.string.exit_exercise_dialog_message),
            Pair(stringResource(R.string.exit_exercise_dialog_back_to_exercise), dialog.onContinue),
            Pair(stringResource(R.string.exit_exercise_dialog_end), dialog.onEnd),
            Pair(stringResource(R.string.exit_exercise_dialog_save), dialog.onSave),
            onDismiss = dialog.onDismiss
        )
        is ExerciseDialog.OutOfTime -> StandardAlertDialog(
            title = stringResource(R.string.out_of_time_dialog_title),
            description = stringResource(R.string.out_of_time_dialog_description),
            Pair(stringResource(R.string.ok_dialog_confirmation), dialog.onAcknowledge),
            onDismiss = dialog.onDismiss
        )
        null -> {}
    }
}

private sealed class PageState(
    val sectionString: String,
    val questionString: String,
    val pageIndex: Int,
    val pageCount: Int,
    val pageMode: PageMode,
    ) {
    class Loading(
        sectionString: String,
        questionString: String,
        pageIndex: Int,
        pageCount: Int,
        pageMode: PageMode,
        val onNavigate: (Int) -> Unit,
        val onPause: (() -> Unit)
    ) : PageState(sectionString, questionString, pageIndex, pageCount, pageMode)

    class Error(
        sectionString: String,
        questionString: String,
        pageIndex: Int,
        pageCount: Int,
        pageMode: PageMode,
        val onNavigate: (Int) -> Unit,
        val onReloadPage: () -> Unit,
        val onPause: (() -> Unit)
    ) : PageState(sectionString, questionString, pageIndex, pageCount, pageMode)

    class Ready(
        sectionString: String,
        questionString: String,
        pageIndex: Int,
        pageCount: Int,
        pageMode: PageMode,
        val descriptions: List<Description>,
        val question: ChildQuestion?,
        val inputPanelExpanded: Boolean,
        val onTogglePanelExpand: () -> Unit,
        val onNavigate: (Int) -> Unit,
        val onPeek: (() -> Unit)?,
        val onPause: (() -> Unit)
    ) : PageState(sectionString, questionString, pageIndex, pageCount, pageMode)

    class Peeking(
        sectionString: String,
        questionString: String,
        pageIndex: Int,
        pageCount: Int,
        pageMode: PageMode,
        val pageIndexToReturn: Int,
        val descriptions: (List<Description>)?,
        val onUnpeek: (Int) -> Unit,
        val onPause: (() -> Unit),
    ) : PageState(sectionString, questionString, pageIndex, pageCount, pageMode)

    class Paused(
        sectionString: String,
        questionString: String,
        pageIndex: Int,
        pageCount: Int,
        pageMode: PageMode.RegularMode,
        val onResume: () -> Unit
    ) : PageState(sectionString, questionString, pageIndex, pageCount, pageMode)
}

private sealed class ImageStatus {
    object Loading : ImageStatus()
    object Error : ImageStatus()
    class Successful(val painter: Painter) : ImageStatus()
}

private sealed class ExerciseDialog(
    val onDismiss: () -> Unit
) {
    class ConfirmExit(
        val onContinue: () -> Unit,
        val onEnd: () -> Unit,
        val onSave: () -> Unit,
        onDismiss: () -> Unit
    ) : ExerciseDialog(onDismiss)
    class OutOfTime(
        val onAcknowledge: () -> Unit,
        onDismiss: () -> Unit
    ) : ExerciseDialog(onDismiss)
}

// business model
sealed class ExerciseState {
    class RegularMode(val timer: ExerciseTimer) : ExerciseState()
    object ReadMode : ExerciseState()
}

private fun ExerciseState.toPageMode(timeRemaining: Time): PageMode = when (this) {
    ExerciseState.ReadMode -> PageMode.ReadMode
    is ExerciseState.RegularMode -> PageMode.RegularMode(timeRemaining)
}

// ui model
sealed class PageMode {
    class RegularMode(val timeRemaining: Time) : PageMode()
    object ReadMode : PageMode()
}

private fun pageIsLoading(
    images: List<String>,
    imageStatuses: Map<String, ImageStatus>
): Boolean = images.any { imageStatuses.getValue(it) == ImageStatus.Loading }

private fun pageIsError(
    images: List<String>,
    imageStatuses: Map<String, ImageStatus>
): Boolean = !pageIsLoading(images, imageStatuses)
    && images.any { imageStatuses.getValue(it) == ImageStatus.Error }

private fun pageIsReady(
    images: List<String>,
    imageStatuses: Map<String, ImageStatus>
): Boolean = images.all { imageStatuses.getValue(it) is ImageStatus.Successful }

private fun PageState.navigationState(): NavigationState = when (this) {
    is PageState.Loading -> NavigationState.Enabled(
        sliderValue = pageIndex,
        sliderRange = 0 until pageCount,
        onNavigate = onNavigate
    )
    is PageState.Error -> NavigationState.Enabled(
        sliderValue = pageIndex,
        sliderRange = 0 until pageCount,
        onNavigate = onNavigate
    )
    is PageState.Ready -> NavigationState.Enabled(
        sliderValue = pageIndex,
        sliderRange = 0 until pageCount,
        onNavigate = onNavigate
    )
    is PageState.Peeking -> NavigationState.Disabled(
        sliderValue = pageIndex,
        sliderRange = 0 until pageCount
    )
    is PageState.Paused -> NavigationState.Disabled(
        sliderValue = pageIndex,
        sliderRange = 0 until pageCount
    )
}

private fun PageState.peekState(): PeekState = when (this) {
    is PageState.Ready -> {
        val onPeek = onPeek
        if (onPeek != null) PeekState.Idle(onPeek) else PeekState.Disabled
    }
    is PageState.Peeking -> PeekState.Peeking(
        { onUnpeek(pageIndexToReturn) }
    )
    else -> PeekState.Disabled
}

private fun PageState.timerState(): TimerState? = when (this) {
    is PageState.Paused -> {
        val pageMode = pageMode
        if (pageMode is PageMode.RegularMode) {
            TimerState.Paused(timeRemaining = pageMode.timeRemaining, onResume = onResume)
        } else null
    }
    is PageState.Error -> {
        val pageMode = pageMode
        if (pageMode is PageMode.RegularMode) {
            TimerState.Counting(timeRemaining = pageMode.timeRemaining, onPause = onPause)
        } else null
    }
    is PageState.Loading -> {
        val pageMode = pageMode
        if (pageMode is PageMode.RegularMode) {
            TimerState.Counting(timeRemaining = pageMode.timeRemaining, onPause = onPause)
        } else null
    }
    is PageState.Peeking -> {
        val pageMode = pageMode
        if (pageMode is PageMode.RegularMode) {
            TimerState.Counting(timeRemaining = pageMode.timeRemaining, onPause = onPause)
        } else null
    }
    is PageState.Ready -> {
        val pageMode = pageMode
        if (pageMode is PageMode.RegularMode) {
            TimerState.Counting(timeRemaining = pageMode.timeRemaining, onPause = onPause)
        } else null
    }
}

private fun PageState.readMode(): Boolean {
    return pageMode is PageMode.ReadMode
}

private val MockMultipleChoiceQuestion = MultipleChoiceQuestion(
    number = 1,
    descriptions = emptyList(),
    inputHint = null,
    options = listOf("Cat", "Dog", "Sheep", "Giraffe"),
    optionType = MultipleChoiceQuestion.OptionType.Text,
    answer = MultipleChoiceQuestion.Answer(userAnswer = null, correctAnswers = listOf(1))
)

private val MockPage = Page(
    sectionString = "6. Instruments, Ornaments, and Techniques",
    pageToPeekIndex = null,
    questionString = "Q 1.2",
    descriptions = listOf(
        Description(Description.Type.Text, "Mock description"),
        Description(Description.Type.Image, "test_score_image"),
        Description(Description.Type.TextEmphasize, "Mock description emphasize"),
        Description(Description.Type.Text, "Mock description"),
        Description(Description.Type.Image, "test_score_image"),
        Description(Description.Type.TextEmphasize, "Mock description emphasize"),
        Description(Description.Type.Text, "Mock description that is extremely fucking long long long long long long long"),
        Description(Description.Type.Image, "test_score_image"),
        Description(Description.Type.TextEmphasize, "Mock description emphasize"),
        Description(Description.Type.Text, "Mock description"),
        Description(Description.Type.Image, "test_score_image"),
        Description(Description.Type.TextEmphasize, "Mock description emphasize")
    ),
    question = MockMultipleChoiceQuestion,
    images = emptyList()
)

private fun newMockPage(color: String, imageCount: Int) = Page(
    sectionString = "Section name",
    pageToPeekIndex = null,
    questionString = "Question",
    descriptions = List(imageCount) { Description(Description.Type.Image, color) },
    question = null,
    images = List(imageCount) { color }
)