package org.meveo.api.dto.document.sign;

import org.meveo.api.dto.BaseEntityDto;

/**
 * DTO class for a document request informations.
 */
public class SignFileRequestDto  extends BaseEntityDto {
    
    /** The Constant serialVersionUID. */ 
    private static final long serialVersionUID = 1L; 
    
    /** The id. */
    private String id;

    /** The name. */
    private String name;
    
    /** The file path. */
    private String filePath;
    
    /** The internal position. */
    private String internalPosition;
    
    /** The external position. */
    private String externalPosition;
    
    /** The internal page. */
    private int internalPage;
    
    /** The external page. */
    private int externalPage;
    
    /** The content. */
    private byte[] content;
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the file path.
     *
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Sets the name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the file path.
     *
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the internalPosition
     */
    public String getInternalPosition() {
        return internalPosition;
    }

    /**
     * @return the externalPosition
     */
    public String getExternalPosition() {
        return externalPosition;
    }

    /**
     * @return the internalPage
     */
    public int getInternalPage() {
        return internalPage;
    }

    /**
     * @return the externalPage
     */
    public int getExternalPage() {
        return externalPage;
    }

    /**
     * @param internalPosition the internalPosition to set
     */
    public void setInternalPosition(String internalPosition) {
        this.internalPosition = internalPosition;
    }

    /**
     * @param externalPosition the externalPosition to set
     */
    public void setExternalPosition(String externalPosition) {
        this.externalPosition = externalPosition;
    }

    /**
     * @param internalPage the internalPage to set
     */
    public void setInternalPage(int internalPage) {
        this.internalPage = internalPage;
    }

    /**
     * @param externalPage the externalPage to set
     */
    public void setExternalPage(int externalPage) {
        this.externalPage = externalPage;
    }
}