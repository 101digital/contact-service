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

        Predicate p = criteriaBuilder.disjunction();

        if (searchText != null) {
            p.getExpressions()
                    .add(criteriaBuilder.or
                                    (criteriaBuilder.equal(root.get("displayName"), searchText),
                                            criteriaBuilder.equal(root.get("mobileNumber"), searchText),
                                            criteriaBuilder.equal(root.get("accountNumber"), searchText),
                                            criteriaBuilder.equal(root.get("userId"), userId)
                                            ));
        }else{
            if (userId != null) {
                p.getExpressions()
                        .add(criteriaBuilder.equal(root.get("userId"), userId));
            }

        }
        return p;
    }
}
