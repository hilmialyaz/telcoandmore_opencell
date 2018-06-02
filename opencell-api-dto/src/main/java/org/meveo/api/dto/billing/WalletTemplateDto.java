package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.catalog.WalletTemplate;

/**
 * The Class WalletTemplateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletTemplateDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2681139334253613359L;

    /** The wallet type. */
    private BillingWalletTypeEnum walletType;

    /** The consumption alert set. */
    private boolean consumptionAlertSet;

    /** The fast rating level. */
    private int fastRatingLevel;

    /** The low balance level. */
    private BigDecimal lowBalanceLevel;

    /**
     * Instantiates a new wallet template dto.
     */
    public WalletTemplateDto() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Instantiates a new wallet template dto.
     *
     * @param walletTemplate the WalletTemplate entity
     */
    public WalletTemplateDto(WalletTemplate walletTemplate) {
        super(walletTemplate);
        walletType = walletTemplate.getWalletType();
        consumptionAlertSet = walletTemplate.isConsumptionAlertSet();
        fastRatingLevel = walletTemplate.getFastRatingLevel();
        lowBalanceLevel = walletTemplate.getLowBalanceLevel();
    }

    /**
     * Gets the wallet type.
     *
     * @return the wallet type
     */
    public BillingWalletTypeEnum getWalletType() {
        return walletType;
    }

    /**
     * Sets the wallet type.
     *
     * @param walletType the new wallet type
     */
    public void setWalletType(BillingWalletTypeEnum walletType) {
        this.walletType = walletType;
    }

    /**
     * Checks if is consumption alert set.
     *
     * @return true, if is consumption alert set
     */
    public boolean isConsumptionAlertSet() {
        return consumptionAlertSet;
    }

    /**
     * Sets the consumption alert set.
     *
     * @param consumptionAlertSet the new consumption alert set
     */
    public void setConsumptionAlertSet(boolean consumptionAlertSet) {
        this.consumptionAlertSet = consumptionAlertSet;
    }

    /**
     * Gets the fast rating level.
     *
     * @return the fast rating level
     */
    public int getFastRatingLevel() {
        return fastRatingLevel;
    }

    /**
     * Sets the fast rating level.
     *
     * @param fastRatingLevel the new fast rating level
     */
    public void setFastRatingLevel(int fastRatingLevel) {
        this.fastRatingLevel = fastRatingLevel;
    }

    /**
     * Gets the low balance level.
     *
     * @return the low balance level
     */
    public BigDecimal getLowBalanceLevel() {
        return lowBalanceLevel;
    }

    /**
     * Sets the low balance level.
     *
     * @param lowBalanceLevel the new low balance level
     */
    public void setLowBalanceLevel(BigDecimal lowBalanceLevel) {
        this.lowBalanceLevel = lowBalanceLevel;
    }

    @Override
    public String toString() {
        return "WalletTemplateDto [code=" + code + ", description=" + description + ", walletType=" + walletType + ", consumptionAlertSet=" + consumptionAlertSet
                + ", fastRatingLevel=" + fastRatingLevel + ", lowBalanceLevel=" + lowBalanceLevel + "]";
    }

}