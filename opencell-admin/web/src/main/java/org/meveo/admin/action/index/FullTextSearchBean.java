package org.meveo.admin.action.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;
import org.meveo.util.view.ESBasedDataModel;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;

@Named
@ConversationScoped
public class FullTextSearchBean implements Serializable {

    private static final long serialVersionUID = -2748591950645172132L;

    @Inject
    private ElasticClient elasticClient;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private BusinessEntityService businessEntityService;

    @Inject
    protected Conversation conversation;

    @Inject
    private Logger log;

    private LazyDataModel<Map<String, Object>> esDataModel;

    /** Search filters. */
    protected Map<String, Object> filters = new HashMap<String, Object>();

    /**
     * DataModel for primefaces lazy loading datatable component.
     * 
     * @return LazyDataModel implementation.
     */
    public LazyDataModel<Map<String, Object>> getEsDataModel() {
        return getEsDataModel(filters);
    }

    public LazyDataModel<Map<String, Object>> getEsDataModel(Map<String, Object> inputFilters) {
        if (esDataModel == null) {

            final Map<String, Object> filters = inputFilters;

            esDataModel = new ESBasedDataModel() {

                private static final long serialVersionUID = -1514374110345615089L;

                @Override
                protected String getFullTextSearchValue(Map<String, Object> loadingFilters) {
                    return (String) filters.get(ESBasedDataModel.FILTER_FULL_TEXT);
                }

                @Override
                protected ElasticClient getElasticClientImpl() {
                    return elasticClient;
                }

                @Override
                public String[] getSearchScope() {

                    // Limit search scope to offers, product, offer template categories, user groups for marketing manager application
                    if (FullTextSearchBean.this.getCurrentUser().hasRole("marketingCatalogManager")
                            || FullTextSearchBean.this.getCurrentUser().hasRole("marketingCatalogVisualization")) {
                        return new String[] { OfferTemplate.class.getName(), ProductTemplate.class.getName(), BundleTemplate.class.getName(), OfferTemplateCategory.class.getName(),
                                UserHierarchyLevel.class.getName() };
                    }
                    return null;
                }
            };
        }

        return esDataModel;
    }

    protected MeveoUser getCurrentUser() {
        return currentUser;
    }

    /**
     * Clean search fields in datatable.
     */
    public void clean() {
        esDataModel = null;
        filters = new HashMap<String, Object>();
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public void preRenderView() {
        beginConversation();
    }

    protected void beginConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    /**
     * Get navigation link and identifier
     * 
     * @param indexName Index name
     * @param type Class simple name or CET code
     * @param id Identifier
     * @return Navigation link/view name to entity's view screen
     */
    @SuppressWarnings("unchecked")
    public String getViewAndId(String indexName, String type, Long id) {

        String viewName = null;

        if (StringUtils.isBlank(type)) {
            type = null;
        }

        ElasticSearchClassInfo scopeInfo = elasticClient.getSearchScopeInfo(indexName, type);

        if (scopeInfo != null) {
            BusinessEntity entity = null;
            if (BusinessEntity.class.isAssignableFrom(scopeInfo.getClazz())) {
                businessEntityService.setEntityClass((Class<BusinessEntity>) scopeInfo.getClazz());
                entity = businessEntityService.findById(id);
            }
            if (entity != null) {
                viewName = BaseBean.getEditViewName(entity.getClass());

                if (getCurrentUser().hasRole("marketingCatalogManager") || getCurrentUser().hasRole("marketingCatalogVisualization")) {
                    viewName = "mm_" + viewName;
                }
            }
        }

        if (viewName == null) {
            log.warn("Could not resolve view and ID for {}/{} {}", indexName, type, id);
            viewName = "fullTextSearch";
        }
        return viewName;
    }
}