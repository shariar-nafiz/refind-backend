package com.shariarunix.refind.repository.specification;

import com.shariarunix.refind.dto.item.ItemSearchCriteria;
import com.shariarunix.refind.entity.Item;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ItemSpecification {

    private ItemSpecification() {
    }

    public static Specification<Item> withCriteria(ItemSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
                String pattern = "%" + criteria.getQuery().trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(titleMatch, descMatch));
            }

            if (criteria.getType() != null) {
                predicates.add(cb.equal(root.get("type"), criteria.getType()));
            }

            if (criteria.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), criteria.getCategoryId()));
            }

            if (criteria.getDivision() != null && !criteria.getDivision().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("location").get("division")), criteria.getDivision().trim().toLowerCase()));
            }

            if (criteria.getDistrict() != null && !criteria.getDistrict().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("location").get("district")), criteria.getDistrict().trim().toLowerCase()));
            }

            if (criteria.getThana() != null && !criteria.getThana().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("location").get("thana")), criteria.getThana().trim().toLowerCase()));
            }

            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            if (criteria.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("incidentDateTime"), criteria.getDateFrom()));
            }

            if (criteria.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("incidentDateTime"), criteria.getDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
