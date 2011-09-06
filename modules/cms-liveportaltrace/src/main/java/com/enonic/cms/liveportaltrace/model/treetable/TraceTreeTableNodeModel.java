package com.enonic.cms.liveportaltrace.model.treetable;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.liveportaltrace.model.DurationModel;

public class TraceTreeTableNodeModel
{
    private String id = "";

    private String text;

    private boolean leaf;

    private boolean expanded = false;

    private String iconCls;

    private List<TraceTreeTableNodeModel> children = new ArrayList<TraceTreeTableNodeModel>();

    private DurationModel duration;

    private String usedCachedResult;

    void setId( String id )
    {
        this.id = id;
    }

    void setText( String text )
    {
        this.text = text;
    }

    void setLeaf( boolean leaf )
    {
        this.leaf = leaf;
    }

    void setExpanded( boolean expanded )
    {
        this.expanded = expanded;
    }

    void setIconCls( String iconCls )
    {
        this.iconCls = iconCls;
    }

    void setChildren( List<TraceTreeTableNodeModel> children )
    {
        this.children = children;
    }

    void addChild( TraceTreeTableNodeModel child )
    {
        children.add( child );
    }

    void setDuration( DurationModel duration )
    {
        this.duration = duration;
    }

    public void setUsedCachedResult( String usedCachedResult )
    {
        this.usedCachedResult = usedCachedResult;
    }

    public String getId()
    {
        return id;
    }

    public String getText()
    {
        return text;
    }

    public boolean isLeaf()
    {
        return leaf;
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    public String getIconCls()
    {
        return iconCls;
    }

    public DurationModel getDuration()
    {
        return duration;
    }

    public String getUsedCachedResult()
    {
        return usedCachedResult;
    }

    public List<TraceTreeTableNodeModel> getChildren()
    {
        return children;
    }
}
