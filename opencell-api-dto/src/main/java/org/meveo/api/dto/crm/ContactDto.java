package org.meveo.api.dto.crm;

import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.AuditableDto;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.NameDto;
import org.meveo.model.communication.CommunicationPolicy;
import org.meveo.model.communication.Message;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.intcrm.ContactGroup;

public class ContactDto extends AccountDto {
	private String email;

	private String assistantName;

	private String assistantPhone;

	private String position;

	private String websiteUrl;
	
	private String importedFrom;

	private String importedBy;
	
	private String socialIdentifier;

	private boolean isVip;

	private boolean isSuspect;

	private boolean agreedToUA;

	private List<ContactGroup> contactGroups = new ArrayList<>();

	private CommunicationPolicy contactPolicy;

	private List<Message> messages;
	
	private AddressBook addressBook;

	public ContactDto () {
		
	}
	
	public ContactDto(Contact contact) {
		this.setName(new NameDto(contact.getName()));
		this.setAddress(new AddressDto(contact.getAddress()));
		this.setCode(contact.getCode());
		this.setAssistantName(contact.getAssistantName());
		this.setAssistantPhone(contact.getAssistantPhone());
		this.setEmail(contact.getEmail());
		this.setPosition(contact.getPosition());
		this.setWebsiteUrl(contact.getWebsiteUrl());
		this.setImportedBy(contact.getImportedBy());
		this.setImportedFrom(contact.getImportedFrom());
		this.setSocialIdentifier(contact.getSocialIdentifier());
		this.setAgreedToUA(contact.isAgreedToUA());
		this.setVip(contact.isVip());
		this.setSuspect(contact.isSuspect());
		this.setAddressBook(contact.getAddressBook());
		this.setMessages(contact.getMessages());
		this.setAuditable(new AuditableDto(contact.getAuditable()));
		this.setCode(contact.getCode());
		this.setId(contact.getId());
		this.setDescription(contact.getDescription());
		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAssistantName() {
		return assistantName;
	}

	public void setAssistantName(String assistantName) {
		this.assistantName = assistantName;
	}

	public String getAssistantPhone() {
		return assistantPhone;
	}

	public void setAssistantPhone(String assistantPhone) {
		this.assistantPhone = assistantPhone;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	
	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getImportedFrom() {
		return importedFrom;
	}

	public void setImportedFrom(String importedFrom) {
		this.importedFrom = importedFrom;
	}

	public String getImportedBy() {
		return importedBy;
	}

	public void setImportedBy(String importedBy) {
		this.importedBy = importedBy;
	}

	public boolean isVip() {
		return isVip;
	}

	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}

	public boolean isSuspect() {
		return isSuspect;
	}

	public void setSuspect(boolean isSuspect) {
		this.isSuspect = isSuspect;
	}

	public boolean isAgreedToUA() {
		return agreedToUA;
	}

	public void setAgreedToUA(boolean agreedToUA) {
		this.agreedToUA = agreedToUA;
	}

	public List<ContactGroup> getContactGroups() {
		return contactGroups;
	}

	public void setContactGroups(List<ContactGroup> contactGroups) {
		this.contactGroups = contactGroups;
	}

	public CommunicationPolicy getContactPolicy() {
		return contactPolicy;
	}

	public void setContactPolicy(CommunicationPolicy contactPolicy) {
		this.contactPolicy = contactPolicy;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public AddressBook getAddressBook() {
		return addressBook;
	}

	public void setAddressBook(AddressBook addressBook) {
		this.addressBook = addressBook;
	}

	public String getSocialIdentifier() {
		return socialIdentifier;
	}

	public void setSocialIdentifier(String socialIdentifier) {
		this.socialIdentifier = socialIdentifier;
	}
	
	
}
