package io.microvibe.booster.core.base.resource;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 扫描到的资源信息
 *
 * @author Qt
 * @since Jun 06, 2018
 */
public class ScannedResource<ID extends Serializable> {
	private ScannedResource parent;
	private Set<ScannedResource> children = new HashSet<>();
	private ID id;
	private String identity;
	private String parentIdentity;
	private String uri;
	private String description;

	public static ScannedResource create(String identity) {
		return create(identity, "", "");
	}

	public static ScannedResource create(String identity, String parentIdentity) {
		return create(identity, parentIdentity, "");
	}

	public static ScannedResource create(String identity, String parentIdentity, String uri) {
		return new ScannedResource().identity(identity).parentIdentity(parentIdentity).uri(uri);
	}

	private ScannedResource() {
	}

	public Set<ScannedResource> children() {
		return children;
	}

	public ScannedResource children(Set<ScannedResource> children) {
		this.children = children;
		return this;
	}

	public ScannedResource parent() {
		return parent;
	}

	public ScannedResource parent(ScannedResource parent) {
		this.parent = parent;
		return this;
	}

	public ID id() {
		return id;
	}

	public ScannedResource id(ID id) {
		this.id = id;
		return this;
	}

	public String identity() {
		return identity;
	}

	public ScannedResource identity(String identity) {
		this.identity = identity;
		return this;
	}

	public String parentIdentity() {
		return parentIdentity;
	}

	public ScannedResource parentIdentity(String parentIdentity) {
		this.parentIdentity = parentIdentity;
		return this;
	}

	public String uri() {
		return uri;
	}

	public ScannedResource uri(String uri) {
		this.uri = uri;
		return this;
	}

	public String description() {
		return description;
	}

	public ScannedResource description(String description) {
		this.description = description;
		return this;
	}
}
