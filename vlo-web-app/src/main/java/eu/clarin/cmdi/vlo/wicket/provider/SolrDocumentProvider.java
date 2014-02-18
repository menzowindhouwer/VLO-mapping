/*
 * Copyright (C) 2014 CLARIN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.clarin.cmdi.vlo.wicket.provider;

import eu.clarin.cmdi.vlo.pojo.QueryFacetsSelection;
import eu.clarin.cmdi.vlo.service.SolrDocumentService;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import org.apache.solr.common.SolrDocument;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.converter.BigDecimalConverter;

/**
 *
 * @author twagoo
 */
public class SolrDocumentProvider implements IDataProvider<SolrDocument> {

    private final SolrDocumentService documentService;
    private final IModel<QueryFacetsSelection> selection;

    public SolrDocumentProvider(SolrDocumentService documentService, IModel<QueryFacetsSelection> selection) {
        this.documentService = documentService;
        this.selection = selection;
    }

    @Override
    public Iterator<? extends SolrDocument> iterator(long first, long count) {
        final List<SolrDocument> documents = documentService.getDocuments(selection.getObject(),
                BigDecimal.valueOf(first).intValueExact(), // safe long->int conversion
                BigDecimal.valueOf(count).intValueExact()); // safe long->int conversion
        return documents.iterator();
    }

    @Override
    public long size() {
        return documentService.getDocumentCount(selection.getObject());
    }

    @Override
    public IModel<SolrDocument> model(SolrDocument object) {
        return new Model(object);
    }

    @Override
    public void detach() {
    }

}
