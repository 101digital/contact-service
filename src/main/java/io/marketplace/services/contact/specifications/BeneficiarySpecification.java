package io.marketplace.services.contact.specifications;

import io.marketplace.services.contact.entity.BeneficiaryEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

        Predicate userFilter = criteriaBuilder.equal(root.get("userId"), userId);

        if(searchText != null){
            Predicate displayNameFilter = criteriaBuilder.equal(root.get("displayName"), searchText);
            Predicate mobileNumberFilter = criteriaBuilder.equal(root.get("mobileNumber"), searchText);
            Predicate accountNumberFilter = criteriaBuilder.equal(root.get("accountNumber"), searchText);

            Predicate searchTextPredicate
                    = criteriaBuilder
                    .or(displayNameFilter, mobileNumberFilter, accountNumberFilter);

            return criteriaBuilder.and(userFilter, searchTextPredicate);
        }else{
            return criteriaBuilder.and(userFilter);
        }
    }
}
