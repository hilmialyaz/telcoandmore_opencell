package org.meveo.apiv2.ordering.order;

import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
public interface Orders extends PaginatedResource<Order> {
}
