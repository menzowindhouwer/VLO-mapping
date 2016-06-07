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
package eu.clarin.cmdi.vlo.wicket.panels;

import eu.clarin.cmdi.vlo.config.VloConfig;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.slf4j.LoggerFactory;

/**
 * A panel with three links:
 * <ul>
 * <li>A link to toggle a text input which shows a bookmarkable link to the
 * current page with parameters representing the current model (permalink)</li>
 * <li>A link to the help pages (/help)</li>
 * <li>A feedback link for the current page (base URL taken from {@link VloConfig#getFeedbackFromUrl()
 * })</li>
 * </ul>
 *
 * @author twagoo
 */
public class TopLinksPanel extends GenericPanel<String> {

    @SpringBean
    private VloConfig vloConfig;

    private final Model<Boolean> linkVisibilityModel;

    public TopLinksPanel(String id, final IModel<String> linkModel) {
        super(id, linkModel);
        this.linkVisibilityModel = new Model<>(false);

        // action to link to request the permalink
        add(createPermaLink("linkRequest"));
        // field that holds the actual link
        add(createLinkField("linkfield", linkModel));

        add(new Link("feedback") {

            @Override
            public void onClick() {
                // construct a feedback URL; this takes the current page URL as a parameter
                // (needs to be URL encoded)
                final String thisPageUrlParam = UrlEncoder.QUERY_INSTANCE.encode(linkModel.getObject(), "UTF-8");
                final String feedbackUrl = vloConfig.getFeedbackFromUrl() + thisPageUrlParam;
                // tell Wicket to redirect to the constructed feedback URL
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(feedbackUrl));
            }
        });
    }

    private Component createPermaLink(String id) {
        // Create a form with a button to toggle permalink rather than an action link
        // to prevent people from confusing the link generated by wicket with
        // the actual permalink generated by the application
        final Form form = new Form(id) {

            @Override
            protected void onConfigure() {
                setVisible(TopLinksPanel.this.getModel() != null);
            }
        };

        form.add(new AjaxFallbackButton("linkRequestButton", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // toggle
                linkVisibilityModel.setObject(!linkVisibilityModel.getObject());

                if (target != null && linkVisibilityModel.getObject()) {
                    target.appendJavaScript("permalinkShown();");
                }

                // callback to react to change
                onChange(target);
            }

        });

        return form;
    }

    private TextField<String> createLinkField(String id, final IModel<String> linkModel) {
        final TextField<String> linkField = new TextField<String>(id, linkModel) {

            @Override
            protected void onConfigure() {
                setVisible(linkVisibilityModel.getObject());
            }

        };
        return linkField;
    }

    protected void onChange(AjaxRequestTarget target) {
        if (target != null) {
            target.add(getPage());
        }
    }

    @Override
    protected void onConfigure() {
        LoggerFactory.getLogger(getClass()).debug("top links panel onconfigure");
    }

}
