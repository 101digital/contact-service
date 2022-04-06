package io.marketplace.services.contact.specifications;

import io.marketplace.services.contact.entity.BeneficiaryEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class BeneficiarySpecification implements Specification<BeneficiaryEntity> {
    private static final long serialVersionUID = 1L;

    private transient String userId;
    private transient String searchText;

    public BeneficiarySpecification(String userId, String searchText) {
        super();
        this.userId = userId;
        this.searchText = searchText;
    }

    @Override
    public Predicate toPredicate(Root<BeneficiaryEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);
        Predicate p = criteriaBuilder.disjunction();
        final List<Predicate> predicateList = new ArrayList<>();

        if(userId != null){
            predicateList.add(criteriaBuilder.equal(root.get("userId"), userId));
        }

        if(searchText != null) {
            final String lowerText = searchText.toLowerCase();

            predicateList.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("displayName")), "%" + lowerText + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("mobileNumber")), "%" + lowerText + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("accountNumber")), "%" + lowerText + "%")));
        }

        Predicate[] predicates = new Predicate[predicateList.size()];
        Predicate predicate = criteriaBuilder.and(predicateList.toArray(predicates));

        p.getExpressions().add(predicate);
        return p;
    }
}
