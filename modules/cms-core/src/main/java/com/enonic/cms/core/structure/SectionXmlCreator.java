package com.enonic.cms.core.structure;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.SiteDao;

public class SectionXmlCreator
{
    private SiteDao siteDao;

    private ContentXMLCreator contentXMLCreator = new ContentXMLCreator();

    public SectionXmlCreator( SiteDao siteDao, CategoryAccessResolver categoryAccessResolver, ContentAccessResolver contentAccessResolver )
    {
        this.siteDao = siteDao;
        contentXMLCreator.setIncludeAccessRightsInfo( false );
        contentXMLCreator.setIncludeRelatedContentsInfo( false );
        contentXMLCreator.setIncludeSectionActivationInfo( true );
        contentXMLCreator.setIncludeRepositoryPathInfo( true );
        contentXMLCreator.setIncludeUserRightsInfo( true, categoryAccessResolver, contentAccessResolver );
        contentXMLCreator.setIncludeVersionsInfoForAdmin( false );
        contentXMLCreator.setIncludeOwnerAndModifierData( false );
        contentXMLCreator.setIncludeDraftInfo( true );
        contentXMLCreator.setIncludeContentData( false );
        contentXMLCreator.setIncludeCategoryData( false );
    }

    public XMLDocument createSectionsDocument( UserEntity runningUser, ContentResultSet contentResultSet )
    {
        final Element sectionsEl = new Element( "sections" );
        List<Section> sectionNameList = getUniqueSectionsSorted( contentResultSet );

        sectionsEl.setAttribute( "count", String.valueOf( sectionNameList.size() ) );
        sectionsEl.setAttribute( "contenttotalcount", String.valueOf( contentResultSet.getTotalCount() ) );

        int totalCount = 0;
        for ( Section section : sectionNameList )
        {
            Element sectionEl = new Element( "section" );

            sectionEl.setAttribute( "sitekey", section.getSiteKey().toString() );
            sectionEl.setAttribute( "sitename", section.getSiteName() );
            sectionEl.setAttribute( "menuitemkey", section.getMenuItemKey().toString() );
            sectionEl.setAttribute( "name", section.getMenuItemName() );
            sectionEl.setAttribute( "path", section.getMenuItemPath() );

            int count = 0;
            for ( ContentEntity content : contentResultSet.getContents() )
            {
                ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
                contentLocationSpecification.setIncludeInactiveLocationsInSection( true );
                contentLocationSpecification.setSiteKey( section.getSiteKey() );
                final ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

                for ( ContentLocation location : contentLocations.getAllLocations() )
                {
                    if ( location.getMenuItemKey().equals( section.getMenuItemKey() ) )
                    {
                        Element contentEl = contentXMLCreator.createSingleContentVersionElement( runningUser, content.getMainVersion() );
                        sectionEl.addContent( contentEl );
                        count++;
                        totalCount++;
                    }
                }
            }
            sectionEl.setAttribute( "sectioncount", String.valueOf( count ) );

            sectionsEl.addContent( sectionEl );
        }
        sectionsEl.setAttribute( "contentcount", String.valueOf( contentResultSet.getLength() ) );
        sectionsEl.setAttribute( "contentinsectioncount", String.valueOf( totalCount ) );

        return XMLDocumentFactory.create( new Document( sectionsEl ) );
    }

    private List<Section> getUniqueSectionsSorted( ContentResultSet contentResultSet )
    {
        Set<Section> uniqueSectionNames = new HashSet<Section>();
        for ( ContentEntity content : contentResultSet.getContents() )
        {
            ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
            contentLocationSpecification.setIncludeInactiveLocationsInSection( true );

            final ContentLocations contentLocations = content.getLocations( contentLocationSpecification );
            for ( ContentLocation contentLocation : contentLocations.getAllLocations() )
            {
                if ( contentLocation.isInSection() )
                {
                    SiteEntity siteEntity = siteDao.findByKey( contentLocation.getSiteKey().toInt() );
                    uniqueSectionNames.add(
                        new Section( contentLocation.getSiteKey(), contentLocation.getMenuItemKey(), contentLocation.getMenuItemName(),
                                     contentLocation.getMenuItemPathAsString(), siteEntity.getName() ) );
                }

            }
        }

        Section[] sectionNameArray = new Section[uniqueSectionNames.size()];
        uniqueSectionNames.toArray( sectionNameArray );
        List<Section> sectionNameList = Arrays.asList( sectionNameArray );

        Collections.sort( sectionNameList, new CaseInsensitiveSectionComparator() );
        return sectionNameList;
    }

}
