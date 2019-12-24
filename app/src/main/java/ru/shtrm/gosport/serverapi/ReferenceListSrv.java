package ru.shtrm.gosport.serverapi;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class ReferenceListSrv {

	@Expose
	private String Name;
	@Expose
	private String ReferenceName;
	@Expose
	private ArrayList<Link> Links = new ArrayList<>();
	@Expose
	private String Id;

	/**
	 *
	 * @return The Name
	 */
	public String getName() {
		return Name;
	}

	/**
	 *
	 * @param Name
	 *            The Name
	 */
	public void setName(String Name) {
		this.Name = Name;
	}

	/**
	 *
	 * @return The ReferenceName
	 */
	public String getReferenceName() {
		return ReferenceName;
	}

	/**
	 *
	 * @param ReferenceName
	 *            The ReferenceName
	 */
	public void setReferenceName(String ReferenceName) {
		this.ReferenceName = ReferenceName;
	}

	/**
	 *
	 * @return The Links
	 */
	public ArrayList<Link> getLinks() {
		return Links;
	}

	/**
	 *
	 * @param Links
	 *            The Links
	 */
	public void setLinks(ArrayList<Link> Links) {
		this.Links = Links;
	}

	/**
	 *
	 * @return The Id
	 */
	public String getId() {
		return Id;
	}

	/**
	 *
	 * @param Id
	 *            The Id
	 */
	public void setId(String Id) {
		this.Id = Id;
	}

	public class Link {

		@Expose
		private String link;

		/**
		 * @return The link
		 */
		public String getLink() {
			return link;
		}

		/**
		 * @param link The link
		 */
		public void setLink(String link) {
			this.link = link;
		}
	}

}
