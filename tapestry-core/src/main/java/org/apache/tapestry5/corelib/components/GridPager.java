// Copyright 2007, 2008, 2009, 2010, 2011, 2012 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.corelib.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentParameterConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.http.Link;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.compatibility.Compatibility;
import org.apache.tapestry5.services.compatibility.Trait;

/**
 * Generates a series of links used to jump to a particular page index within the overall data set.
 *
 * @tapestrydoc
 */
@Events(InternalConstants.GRID_INPLACE_UPDATE + " (internal event)")
public class GridPager
{
    /**
     * The source of the data displayed by the grid (this is used to determine {@link GridDataSource#getAvailableRows()
     * how many rows are available}, which in turn determines the page count).
     */
    @Parameter(required = true)
    private GridDataSource source;

    /**
     * The number of rows displayed per page.
     */
    @Parameter(required = true)
    private int rowsPerPage;

    /**
     * The current page number (indexed from 1).
     */
    @Parameter(required = true)
    private int currentPage;

    /**
     * Number of pages before and after the current page in the range. The pager always displays links for 2 * range + 1
     * pages, unless that's more than the total number of available pages.
     */
    @Parameter(BindingConstants.SYMBOL + ":" + ComponentParameterConstants.GRIDPAGER_PAGE_RANGE)
    private int range;

    /**
     * If not null, then each link is output as a link to update the specified zone.
     */
    @Parameter
    private String zone;

    private int lastIndex;

    private int maxPages;

    @Inject
    private ComponentResources resources;

    @Inject
    private Messages messages;

    @Inject
    private Request request;
    
    @Inject
    private Compatibility compatibility;

    void beginRender(MarkupWriter writer)
    {
        int availableRows = source.getAvailableRows();

        maxPages = ((availableRows - 1) / rowsPerPage) + 1;

        if (maxPages < 2) return;

        writer.element("ul", "class", "pagination");

        if (zone != null)
        {
            writer.attributes("data-inplace-grid-links", true);
        }

        lastIndex = 0;

        for (int i = 1; i <= 2; i++)
            writePageLink(writer, i);

        int low = currentPage - range;
        int high = currentPage + range;

        if (low < 1)
        {
            low = 1;
            high = 2 * range + 1;
        } else
        {
            if (high > maxPages)
            {
                high = maxPages;
                low = high - 2 * range;
            }
        }

        for (int i = low; i <= high; i++)
            writePageLink(writer, i);

        for (int i = maxPages - 1; i <= maxPages; i++)
            writePageLink(writer, i);

        writer.end();    // ul
    }

    private void writePageLink(MarkupWriter writer, int pageIndex)
    {
        if (pageIndex < 1 || pageIndex > maxPages) return;

        if (pageIndex <= lastIndex) return;

        final boolean isBootstrap4 = isBootstrap4();
        
        if (pageIndex != lastIndex + 1)
        {
            writer.element("li", "class", isBootstrap4 ? "disabled page-item" : "disabled");
            writer.element("a", "href", "#", "aria-disabled", "true");
            addClassAttributeToPageLinkIfNeeded(writer, isBootstrap4);
            writer.write(" ... ");
            writer.end();
            writer.end();
        }

        lastIndex = pageIndex;

        if (pageIndex == currentPage)
        {
            writer.element("li", "aria-current", "page", "class", isBootstrap4 ? "active page-item" : "active");
            writer.element("a", "href", "#", "aria-disabled", "true");
            addClassAttributeToPageLinkIfNeeded(writer, isBootstrap4);            
            writer.write(Integer.toString(pageIndex));
            writer.end();
            writer.end();
            return;
        }

        writer.element("li");
        if (isBootstrap4)
        {
            writer.getElement().attribute("class", "page-item");
        }

        Link link = resources.createEventLink(EventConstants.ACTION, pageIndex);

        if (zone != null)
        {
            link.addParameter("t:inplace", "true");
        }

        writer.element("a",
                "href", link,
                "data-update-zone", zone,
                "title", messages.format("core-goto-page", pageIndex));

        addClassAttributeToPageLinkIfNeeded(writer, isBootstrap4);

        writer.write(Integer.toString(pageIndex));

        writer.end();

        writer.end();   // li
    }

    private void addClassAttributeToPageLinkIfNeeded(MarkupWriter writer, final boolean isBootstrap4) {
        if (isBootstrap4)
        {
            writer.getElement().attribute("class", "page-link");
        }
    }

    /**
     * Repaging event handler.
     */
    boolean onAction(int newPage)
    {
        // TODO: Validate newPage in range

        currentPage = newPage;

        if (request.isXHR())
        {
            resources.triggerEvent(InternalConstants.GRID_INPLACE_UPDATE, null, null);
        }

        return true;     // abort event
    }
    
    protected boolean isBootstrap4()
    {
        return compatibility.enabled(Trait.BOOTSTRAP_4);
    }
}
