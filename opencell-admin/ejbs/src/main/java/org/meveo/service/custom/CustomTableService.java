package org.meveo.service.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.SQLQuery;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.customEntities.CustomTableRecord;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;

@Stateless
public class CustomTableService extends NativePersistenceService {

    /**
     * File prefix indicating that imported data should be appended to exiting data
     */
    public static final String FILE_APPEND = "_append";

    @Inject
    private ElasticClient elasticClient;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @EJB
    private CustomTableService customTableService;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    // public void createClass(String customTableName) {
    //
    // ClassPool pool = ClassPool.getDefault();
    // ClassClassPath classPath = new ClassClassPath(this.getClass());
    // pool.insertClassPath(classPath);
    // log.error("AKK inserted classpath {}", classPath);
    // CtClass cc = pool.makeClass("org.meveo." + customTableName);
    //
    // try {
    // CtField f = new CtField(CtClass.intType, "z", cc);
    // cc.addField(f);
    //
    // cc.addMethod(CtNewMethod.getter("getZ", f));
    // cc.addMethod(CtNewMethod.setter("setZ", f));
    // cc.addMethod(CtNewMethod.make("public String toString() {return \" \"+z;}", cc));
    //
    // cc.writeFile("C:\\andrius\\programs\\wildfly-10.1.0.Final\\standalone\\deployments\\opencell.war\\WEB-INF\\classes");
    //
    // Class clazz = cc.toClass();
    // Object instance = clazz.newInstance();
    // Field field = ReflectionUtils.getField(clazz, "z");
    // field.setAccessible(true);
    // field.set(instance, 10);
    // log.error("AKK field value is {}", field.get(instance));
    //
    // Object value = getEntityManager().createNativeQuery("select id from cust_cet", CustomTableRecord.class).getSingleResult();
    //
    // log.error("AKK Value from DB is {} {}", value);// , field.get(value));
    //
    // } catch (
    //
    // Exception e) {
    // log.error("AKK Failed to create a new Class {}", customTableName, e);
    // }
    //
    // }

    @Override
    public Long create(String tableName, Map<String, Object> values) throws BusinessException {

        Long id = super.create(tableName, values, true); // Force to return ID as we need it to retrieve data for Elastic Search population
        elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, values, false, true);

        return id;
    }

    /**
     * Insert multiple values into table
     * 
     * @param tableName Table name to insert values to
     * @param values A list of values to insert
     * @throws BusinessException General exception
     */
    public void create(String tableName, List<Map<String, Object>> values) throws BusinessException {

        for (Map<String, Object> value : values) {
            Long id = super.create(tableName, value, true); // Force to return ID as we need it to retrieve data for Elastic Search population
            elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, value, false, false);
        }

        elasticClient.flushChanges();
    }

    /**
     * Insert multiple values into table with optionally not updating ES. Will execute in a new transaction
     * 
     * @param tableName Table name to insert values to
     * @param values Values to insert
     * @param updateES Should Elastic search be updated during record creation. If false, ES population must be done outside this call.
     * @throws BusinessException General exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createInNewTx(String tableName, List<Map<String, Object>> values, boolean updateES) throws BusinessException {

        // Insert record to db, with ID returned, but flush to ES after the values are processed
        if (updateES) {

            create(tableName, values);

        } else {
            super.create(tableName, values);
        }
    }

    @Override
    public void update(String tableName, Map<String, Object> values) throws BusinessException {
        super.update(tableName, values);
        elasticClient.createOrUpdate(CustomTableRecord.class, tableName, values.get(NativePersistenceService.FIELD_ID), values, false, true);
    }

    /**
     * Update multiple values in a table. Record is identified by an "id" field value.
     * 
     * @param tableName Table name to update values
     * @param values Values to update. Must contain an 'id' field.
     * @throws BusinessException General exception
     */
    public void update(String tableName, List<Map<String, Object>> values) throws BusinessException {

        for (Map<String, Object> value : values) {
            super.update(tableName, value);
            elasticClient.createOrUpdate(CustomTableRecord.class, tableName, value.get(NativePersistenceService.FIELD_ID), value, false, false);
        }
        elasticClient.flushChanges();
    }

    @Override
    public void updateValue(String tableName, Long id, String fieldName, Object value) throws BusinessException {
        super.updateValue(tableName, id, fieldName, value);
        elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, MapUtils.putAll(new HashMap<String, Object>(), new Object[] { fieldName, value }), true, true);
    }

    @Override
    public void disable(String tableName, Long id) throws BusinessException {
        super.disable(tableName, id);
        elasticClient.remove(CustomTableRecord.class, tableName, id, true);
    }

    @Override
    public void disable(String tableName, Set<Long> ids) throws BusinessException {

        super.disable(tableName, ids);
        elasticClient.remove(CustomTableRecord.class, tableName, ids, true);
    }

    @Override
    public void enable(String tableName, Long id) throws BusinessException {
        super.enable(tableName, id);
        Map<String, Object> values = findById(tableName, id);
        elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, values, false, true);
    }

    @Override
    public void enable(String tableName, Set<Long> ids) throws BusinessException {
        super.enable(tableName, ids);
        for (Long id : ids) {
            Map<String, Object> values = findById(tableName, id);
            elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, values, false, false);
        }
        elasticClient.flushChanges();
    }

    @Override
    public void remove(String tableName, Long id) throws BusinessException {
        super.remove(tableName, id);
        elasticClient.remove(CustomTableRecord.class, tableName, id, true);
    }

    @Override
    public void remove(String tableName, Set<Long> ids) throws BusinessException {
        super.remove(tableName, ids);
        elasticClient.remove(CustomTableRecord.class, tableName, ids, true);
    }

    @Override
    public void remove(String tableName) throws BusinessException {
        super.remove(tableName);
        elasticClient.remove(CustomTableRecord.class, tableName);
    }

    /**
     * Export data into a file into exports directory. Filename is in the following format: &lt;db table name&gt;_id_&lt;formated date&gt;.csv
     * 
     * @param customEntityTemplate Custom table definition
     * @param config Pagination and search criteria
     * @return A future with a file name where the data will be exported to or an exception occurred
     * @throws BusinessException General exception
     */
    @Asynchronous
    @SuppressWarnings("unchecked")
    public Future<DataImportExportStatistics> exportData(CustomEntityTemplate customEntityTemplate, PaginationConfiguration config) throws BusinessException {

        try {
            QueryBuilder queryBuilder = getQuery(customEntityTemplate.getDbTablename(), config);

            SQLQuery query = queryBuilder.getNativeQuery(getEntityManager(), true);

            int firstRow = 0;
            int nrItemsFound = 0;

            ParamBean parambean = paramBeanFactory.getInstance();
            String providerRoot = parambean.getChrootDir(currentUser.getProviderCode());
            String exportDir = providerRoot + File.separator + "exports" + File.separator;

            File exportsDirFile = new File(exportDir);

            File exportFile = new File(exportDir + customEntityTemplate.getDbTablename() + DateUtils.formatDateWithPattern(new Date(), "_yyyy-MM-dd_HH-mm-ss") + ".csv");

            if (!exportsDirFile.exists()) {
                exportsDirFile.mkdirs();
            }

            Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo());

            if (cfts == null || cfts.isEmpty()) {
                throw new ValidationException("No fields are defined for custom table " + customEntityTemplate.getDbTablename(), "customTable.noFields");
            }

            List<CustomFieldTemplate> fields = new ArrayList<>(cfts.values());

            Collections.sort(fields, new Comparator<CustomFieldTemplate>() {

                @Override
                public int compare(CustomFieldTemplate cft1, CustomFieldTemplate cft2) {
                    int pos1 = cft1.getGUIFieldPosition();
                    int pos2 = cft2.getGUIFieldPosition();

                    return pos1 - pos2;
                }
            });

            ObjectWriter oWriter = getCSVWriter(fields);

            try (FileWriter fileWriter = new FileWriter(exportFile)) {

                SequenceWriter sWriter = oWriter.writeValues(fileWriter);

                do {
                    queryBuilder.applyPagination(query, firstRow, 500);
                    List<Map<String, Object>> values = query.list();
                    nrItemsFound = values.size();
                    firstRow = firstRow + 500;

                    sWriter.writeAll(values);

                } while (nrItemsFound == 500);

            } catch (IOException e) {
                log.error("Failed to write {} table data to a file {}", customEntityTemplate.getDbTablename(), exportFile.getAbsolutePath(), e);
                throw new BusinessException(e);
            }

            return new AsyncResult<DataImportExportStatistics>(new DataImportExportStatistics(exportFile.getAbsolutePath().substring(providerRoot.length())));

        } catch (Exception e) {
            return new AsyncResult<DataImportExportStatistics>(new DataImportExportStatistics(e));
        }
    }

    /**
     * Import data into custom table
     * 
     * @param customEntityTemplate Custom table definition
     * @param file Data file
     * @param append True if data should be appended to the existing data
     * @return Number of records imported
     * @throws BusinessException General business exception
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int importData(CustomEntityTemplate customEntityTemplate, File file, boolean append) throws BusinessException {

        try (FileInputStream inputStream = new FileInputStream(file)) {
            return importData(customEntityTemplate, inputStream, append);

        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * Import data into custom table in asynchronous mode
     * 
     * @param customEntityTemplate Custom table definition
     * @param inputStream Data stream
     * @param append True if data should be appended to the existing data
     * @return A future with a number of records imported or exception occurred
     * @throws BusinessException General business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<DataImportExportStatistics> importDataAsync(CustomEntityTemplate customEntityTemplate, InputStream inputStream, boolean append) throws BusinessException {

        try {
            int itemsImported = importData(customEntityTemplate, inputStream, append);
            return new AsyncResult<DataImportExportStatistics>(new DataImportExportStatistics(itemsImported));

        } catch (Exception e) {
            return new AsyncResult<DataImportExportStatistics>(new DataImportExportStatistics(e));
        }
    }

    /**
     * Import data into custom table
     * 
     * @param customEntityTemplate Custom table definition
     * @param inputStream Data stream
     * @param append True if data should be appended to the existing data
     * @return Number of records imported
     * @throws BusinessException General business exception
     */
    @SuppressWarnings("rawtypes")
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int importData(CustomEntityTemplate customEntityTemplate, InputStream inputStream, boolean append) throws BusinessException {

        // Custom table fields. Fields will be sorted by their GUI 'field' position.
        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo());
        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table " + customEntityTemplate.getDbTablename(), "customTable.noFields");
        }
        List<CustomFieldTemplate> fields = new ArrayList<>(cfts.values());

        Collections.sort(fields, new Comparator<CustomFieldTemplate>() {

            @Override
            public int compare(CustomFieldTemplate cft1, CustomFieldTemplate cft2) {
                int pos1 = cft1.getGUIFieldPosition();
                int pos2 = cft2.getGUIFieldPosition();

                return pos1 - pos2;
            }
        });

        Map<String, Class> fieldTypes = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            fieldTypes.put(cft.getCode(), cft.getFieldType().getDataClass());
            fieldTypes.put(cft.getDbFieldname(), cft.getFieldType().getDataClass());
        }

        String tableName = customEntityTemplate.getDbTablename();
        int importedLines = 0;
        int importedLinesTotal = 0;
        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();

        ObjectReader oReader = getCSVReader(fields);

        // Delete current data first if in override mode
        if (!append) {
            customTableService.remove(tableName);
        }

        // Update ES in batch way might be faster - reconstructed from a table
        boolean updateESImediately = false;

        try (Reader reader = new InputStreamReader(inputStream)) {

            MappingIterator<Map<String, Object>> mappingIterator = oReader.readValues(reader);

            while (mappingIterator.hasNext()) {

                // Save to DB every 500 records
                if (importedLines >= 500) {

                    values = convertValues(values, fieldTypes, false);
                    customTableService.createInNewTx(tableName, values, updateESImediately);

                    values.clear();
                    importedLines = 0;
                }

                Map<String, Object> lineValues = mappingIterator.next();
                values.add(lineValues);

                importedLines++;
                importedLinesTotal++;

                if (importedLinesTotal % 30000 == 0) {
                    log.trace("Imported {} lines to {} table", importedLinesTotal, tableName);
                }
            }

            // Save to DB remaining records
            values = convertValues(values, fieldTypes, false);
            customTableService.createInNewTx(tableName, values, updateESImediately);

            // Re-populate ES index
            if (!updateESImediately) {
                elasticClient.populateAll(currentUser, CustomTableRecord.class, customEntityTemplate.getCode());
            }

            log.info("Imported {} lines to {} table", importedLinesTotal, tableName);

        } catch (RuntimeJsonMappingException e) {
            throw new ValidationException("Invalid file format", "message.upload.fail.invalidFormat", e);

        } catch (IOException e) {
            throw new BusinessException(e);
        }

        return importedLinesTotal;
    }

    /**
     * Import data into custom table
     * 
     * @param customEntityTemplate Custom table definition
     * @param values A list of records to import. Each record is a map of values with field name as a map key and field value as a value.
     * @param append True if data should be appended to the existing data
     * @return Number of records imported
     * @throws BusinessException General business exception
     */
    @SuppressWarnings("rawtypes")
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int importData(CustomEntityTemplate customEntityTemplate, List<Map<String, Object>> values, boolean append) throws BusinessException {

        // Custom table fields. Fields will be sorted by their GUI 'field' position.
        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo());
        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table " + customEntityTemplate.getDbTablename(), "customTable.noFields");
        }
        List<CustomFieldTemplate> fields = new ArrayList<>(cfts.values());

        Collections.sort(fields, new Comparator<CustomFieldTemplate>() {

            @Override
            public int compare(CustomFieldTemplate cft1, CustomFieldTemplate cft2) {
                int pos1 = cft1.getGUIFieldPosition();
                int pos2 = cft2.getGUIFieldPosition();

                return pos1 - pos2;
            }
        });

        Map<String, Class> fieldTypes = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            fieldTypes.put(cft.getCode(), cft.getFieldType().getDataClass());
            fieldTypes.put(cft.getDbFieldname(), cft.getFieldType().getDataClass());
        }

        String tableName = customEntityTemplate.getDbTablename();
        int importedLines = 0;
        int importedLinesTotal = 0;
        List<Map<String, Object>> valuesPartial = new ArrayList<Map<String, Object>>();

        // Delete current data first if in override mode
        if (!append) {
            customTableService.remove(tableName);
        }

        // By default will update ES immediately. If more than 100 records are being updated, ES will be updated in batch way - reconstructed from a table
        boolean updateESImediately = append;
        if (values.size() > 100) {
            updateESImediately = false;
        }

        try {

            for (Map<String, Object> value : values) {

                // Save to DB every 1000 records
                if (importedLines >= 1000) {

                    valuesPartial = convertValues(valuesPartial, fieldTypes, false);
                    customTableService.createInNewTx(tableName, valuesPartial, updateESImediately);

                    valuesPartial.clear();
                    importedLines = 0;
                }

                valuesPartial.add(value);

                importedLines++;
                importedLinesTotal++;
            }

            // Save to DB remaining records
            valuesPartial = convertValues(valuesPartial, fieldTypes, false);
            customTableService.createInNewTx(tableName, valuesPartial, updateESImediately);

            // Repopulate ES index
            if (!updateESImediately) {
                elasticClient.populateAll(currentUser, CustomTableRecord.class, customEntityTemplate.getCode());
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return importedLinesTotal;
    }

    /**
     * Get the CSV file reader. Schema is created from field's dbFieldname values.
     * 
     * @param fields Custom table fields definition
     * @return The CSV file reader
     */
    private ObjectReader getCSVReader(Collection<CustomFieldTemplate> fields) {
        CsvSchema.Builder builder = CsvSchema.builder();

        builder.addColumn(NativePersistenceService.FIELD_ID, ColumnType.NUMBER);

        for (CustomFieldTemplate cft : fields) {
            builder.addColumn(cft.getDbFieldname(),
                cft.getFieldType() == CustomFieldTypeEnum.LONG || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE ? ColumnType.NUMBER : ColumnType.STRING);
        }

        CsvSchema schema = builder.setUseHeader(true).setStrictHeaders(true).setReorderColumns(true).build();
        CsvMapper mapper = new CsvMapper();
        return mapper.readerFor(Map.class).with(schema);
    }

    /**
     * Get the CSV file writer. Schema is created from field's dbFieldname values.
     * 
     * @param fields Custom table fields definition
     * @return The CSV file reader
     */
    private ObjectWriter getCSVWriter(Collection<CustomFieldTemplate> fields) {
        CsvSchema.Builder builder = CsvSchema.builder();

        builder.addColumn(NativePersistenceService.FIELD_ID, ColumnType.NUMBER);

        for (CustomFieldTemplate cft : fields) {
            builder.addColumn(cft.getDbFieldname(),
                cft.getFieldType() == CustomFieldTypeEnum.LONG || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE ? ColumnType.NUMBER : ColumnType.STRING);
        }

        CsvSchema schema = builder.setUseHeader(true).build();
        CsvMapper mapper = new CsvMapper();

        return mapper.writerFor(Map.class).with(schema).with(new SimpleDateFormat(ParamBean.getInstance().getDateTimeFormat(appProvider.getCode())))
            .with(Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    }

    /**
     * Execute a search on given fields for given query values. See ElasticClient.search() for a query format.
     *
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param queryValues Fields and values to match
     * @param from Pagination - starting record. Defaults to 0.
     * @param size Pagination - number of records per page. Defaults to ElasticClient.DEFAULT_SEARCH_PAGE_SIZE.
     * @param sortFields - Fields to sort by. If omitted, will sort by score. If search query contains a 'closestMatch' expression, sortFields and sortOrder will be overwritten
     *        with a corresponding field and descending order.
     * @param sortOrders Sorting orders
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @return Search result
     * @throws BusinessException General business exception
     */
    public List<Map<String, Object>> search(String cetCodeOrTablename, Map<String, Object> queryValues, Integer from, Integer size, String[] sortFields, SortOrder[] sortOrders,
            String[] returnFields) throws BusinessException {

        ElasticSearchClassInfo classInfo = new ElasticSearchClassInfo(CustomTableRecord.class, cetCodeOrTablename);
        SearchResponse searchResult = elasticClient.search(queryValues, from, size, sortFields, sortOrders, returnFields, Arrays.asList(classInfo));

        if (searchResult == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> responseValues = new ArrayList<>();

        searchResult.getHits().forEach(hit -> {
            Map<String, Object> values = new HashMap<>();
            responseValues.add(values);

            if (hit.getFields() != null && hit.getFields().values() != null && !hit.getFields().values().isEmpty()) {
                for (DocumentField field : hit.getFields().values()) {
                    if (field.getValues() != null) {
                        if (field.getValues().size() > 1) {
                            values.put(field.getName(), field.getValues());
                        } else {
                            values.put(field.getName(), field.getValue());
                        }
                    }
                }

            } else if (hit.getSourceAsMap() != null) {
                values.putAll(hit.getSourceAsMap());
            }
        });

        // log.debug("AKK ES search result values are {}", responseValues);
        return responseValues;
    }

    /**
     * Get field value of the first record matching search criteria
     * 
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param fieldToReturn Field value to return
     * @param queryValues Search criteria with condition/field name as a key and field value as a value. See ElasticClient.search() for a query format.
     * @return A field value
     * @throws BusinessException General exception
     */
    public Object getValue(String cetCodeOrTablename, String fieldToReturn, Map<String, Object> queryValues) throws BusinessException {

        Map<String, Object> values = new HashMap<>(queryValues);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_ID }, new SortOrder[] { SortOrder.DESC }, new String[] { fieldToReturn });

        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0).get(fieldToReturn);
        }
    }

    /**
     * Get field value of the first record matching search criteria for a given date. Applicable to custom tables that contain 'valid_from' and 'valid_to' fields
     * 
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param fieldToReturn Field value to return
     * @param date Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param queryValues Search criteria with condition/field name as a key and field value as a value. See ElasticClient.search() for a query format.
     * @return A field value
     * @throws BusinessException General exception
     */
    public Object getValue(String cetCodeOrTablename, String fieldToReturn, Date date, Map<String, Object> queryValues) throws BusinessException {

        Map<String, Object> values = new HashMap<>(queryValues);
        values.put("minmaxRange valid_from valid_to", date);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_VALID_PRIORITY, FIELD_VALID_FROM, FIELD_ID },
            new SortOrder[] { SortOrder.DESC, SortOrder.DESC, SortOrder.DESC }, new String[] { fieldToReturn });

        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0).get(fieldToReturn);
        }
    }

    /**
     * Get field values of the first record matching search criteria
     * 
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param fieldsToReturn Field values to return. Optional. If not provided all fields will be returned.
     * @param queryValues Search criteria with condition/field name as a key and field value as a value. See ElasticClient.search() for a query format.
     * @return A map of values with field name as a key and field value as a value. Note field value is always of String data type.
     * @throws BusinessException General exception
     */
    public Map<String, Object> getValues(String cetCodeOrTablename, String[] fieldsToReturn, Map<String, Object> queryValues) throws BusinessException {

        Map<String, Object> values = new HashMap<>(queryValues);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_ID }, new SortOrder[] { SortOrder.DESC }, fieldsToReturn);

        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    /**
     * Get field values of the first record matching search criteria for a given date. Applicable to custom tables that contain 'valid_from' and 'valid_to' fields
     * 
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param fieldsToReturn Field values to return. Optional. If not provided all fields will be returned.
     * @param date Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param queryValues Search criteria with condition/field name as a key and field value as a value. See ElasticClient.search() for a query format.
     * @return A map of values with field name as a key and field value as a value. Note field value is always of String data type.
     * @throws BusinessException General exception
     */
    public Map<String, Object> getValues(String cetCodeOrTablename, String[] fieldsToReturn, Date date, Map<String, Object> queryValues) throws BusinessException {

        Map<String, Object> values = new HashMap<>(queryValues);
        values.put("minmaxRange valid_from valid_to", date);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_VALID_PRIORITY, FIELD_VALID_FROM, FIELD_ID },
            new SortOrder[] { SortOrder.DESC, SortOrder.DESC, SortOrder.DESC }, fieldsToReturn);

        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    /**
     * Convert values to a data type matching field definition
     * 
     * @param values A map of values with field name of customFieldTemplate code as a key and field value as a value
     * @param fields Field definitions
     * @param discardNull If True, null values will be discarded
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String, Object>> convertValues(List<Map<String, Object>> values, Collection<CustomFieldTemplate> fields, boolean discardNull) throws ValidationException {

        if (values == null) {
            return null;
        }

        Map<String, Class> fieldTypes = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            fieldTypes.put(cft.getCode(), cft.getFieldType().getDataClass());
            fieldTypes.put(cft.getDbFieldname(), cft.getFieldType().getDataClass());
        }

        return convertValues(values, fieldTypes, discardNull);

    }

    /**
     * Convert values to a data type matching field definition
     * 
     * @param values A map of values with field name of customFieldTemplate code as a key and field value as a value
     * @param fields Field definitions with field name or field code as a key and data class as a value
     * @param discardNull If True, null values will be discarded
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String, Object>> convertValues(List<Map<String, Object>> values, Map<String, Class> fields, boolean discardNull) throws ValidationException {

        if (values == null) {
            return null;
        }
        List<Map<String, Object>> convertedValues = new LinkedList<>();

        String[] datePatterns = new String[] { DateUtils.DATE_TIME_PATTERN, paramBean.getDateTimeFormat(), DateUtils.DATE_PATTERN, paramBean.getDateFormat() };

        for (Map<String, Object> value : values) {
            convertedValues.add(convertValue(value, fields, discardNull, datePatterns));
        }

        return convertedValues;
    }

    /**
     * Convert values to a data type matching field definition
     * 
     * @param values A map of values with customFieldTemplate code or db field name as a key and field value as a value.
     * @param fields Field definitions
     * @param discardNull If True, null values will be discarded
     * @param datePatterns Optional. Date patterns to apply to a date type field. Conversion is attempted in that order until a valid date is matched.If no values are provided, a
     *        standard date and time and then date only patterns will be applied.
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Object> convertValue(Map<String, Object> values, Collection<CustomFieldTemplate> fields, boolean discardNull, String[] datePatterns)
            throws ValidationException {

        if (values == null) {
            return null;
        }

        Map<String, Class> fieldTypes = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            fieldTypes.put(cft.getCode(), cft.getFieldType().getDataClass());
            fieldTypes.put(cft.getDbFieldname(), cft.getFieldType().getDataClass());
        }

        return convertValue(values, fieldTypes, discardNull, datePatterns);
    }

    /**
     * Convert single record values to a data type matching field definition
     * 
     * @param values A map of values with customFieldTemplate code or db field name as a key and field value as a value.
     * @param fields Field definitions with field name or field code as a key and data class as a value
     * @param discardNull If True, null values will be discarded
     * @param datePatterns Optional. Date patterns to apply to a date type field. Conversion is attempted in that order until a valid date is matched.If no values are provided, a
     *        standard date and time and then date only patterns will be applied.
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    @SuppressWarnings("rawtypes")
    private Map<String, Object> convertValue(Map<String, Object> values, Map<String, Class> fields, boolean discardNull, String[] datePatterns) throws ValidationException {

        if (values == null) {
            return null;
        }

        Map<String, Object> valuesConverted = new HashMap<>();

        // Handle ID field
        Object id = values.get(FIELD_ID);
        if (id != null) {
            valuesConverted.put(FIELD_ID, castValue(id, Long.class, false, datePatterns));
        }

        // Convert field based on data type
        if (fields != null) {
            for (Entry<String, Object> valueEntry : values.entrySet()) {

                String key = valueEntry.getKey();
                if (key.equals(FIELD_ID)) {
                    continue; // Was handled before already
                }
                if (valueEntry.getValue() == null && !discardNull) {
                    valuesConverted.put(key, null);

                } else if (valueEntry.getValue() != null) {

                    String[] fieldInfo = key.split(" ");
                    // String condition = fieldInfo.length == 1 ? null : fieldInfo[0];
                    String fieldName = fieldInfo.length == 1 ? fieldInfo[0] : fieldInfo[1]; // field name here can be a db field name or a custom field code

                    Class dataClass = fields.get(fieldName);
                    if (dataClass == null) {
                        throw new ValidationException("No field definition " + fieldName + " was found");
                    }
                    Object value = castValue(valueEntry.getValue(), dataClass, false, datePatterns);

                    // Replace cft code with db field name if needed
                    String dbFieldname = CustomFieldTemplate.getDbFieldname(fieldName);
                    if (!fieldName.equals(dbFieldname)) {
                        key = key.replaceAll(fieldName, dbFieldname);
                    }
                    valuesConverted.put(key, value);
                }
            }

        }
        return valuesConverted;
    }
}