package com.donald.musictheoryapp.Utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CursorBuilder
{
    private final SQLiteDatabase m_DB;
    private String m_Table;
    private final ArrayList<String> m_ColumnsToGetFrom;
    private final StringBuilder m_ColumnsToCompare;
    private final ArrayList<String> m_ValuesToMatch;
    private String m_Arrangement;

    private CursorBuilder(SQLiteDatabase db)
    {
        m_DB = db;
        m_ColumnsToGetFrom = new ArrayList<>();
        m_ValuesToMatch = new ArrayList<>();
        m_ColumnsToCompare = new StringBuilder();
    };

    public static CursorBuilder cursor(SQLiteDatabase db) { return new CursorBuilder(db); }

    public CursorBuilder fromTable(String table) { m_Table = table; return this; }

    public CursorBuilder returnColumn(String column) { m_ColumnsToGetFrom.add(column); return this;}

    public CursorBuilder ifValueFromColumn(String column)
    {
        m_ColumnsToCompare.append(column);
        m_ColumnsToCompare.append(" = ?");
        return this;
    }

    public CursorBuilder matches(String value)
    {
        m_ValuesToMatch.add(value);
        return this;
    }

    public CursorBuilder and()
    {
        m_ColumnsToCompare.append(" AND ");
        return this;
    }

    public CursorBuilder orderedBy(String column)
    {
        m_Arrangement = column + " ";
        return this;
    }

    public CursorBuilder ascending()
    {
        m_Arrangement += "DESC";
        return this;
    }

    public CursorBuilder descending()
    {
        m_Arrangement += "ASC";
        return this;
    }

    public CursorBuilder withoutOrdering()
    {
        m_Arrangement = null;
        return this;
    }

    public Cursor build()
    {
        String[] columnsToGetFrom = new String[m_ColumnsToGetFrom.size()];
        columnsToGetFrom = m_ColumnsToGetFrom.toArray(columnsToGetFrom);

        String columnsToCompare = m_ColumnsToCompare.toString();

        String[] valuesToMatch = new String[m_ValuesToMatch.size()];
        valuesToMatch = m_ValuesToMatch.toArray(valuesToMatch);

        return m_DB.query(
            m_Table,
            columnsToGetFrom,
            columnsToCompare,
            valuesToMatch,
            null,
            null,
            m_Arrangement
        );
    }
}
