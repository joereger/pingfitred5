package com.pingfit.dao.hibernate;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

public class CustomReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {
    public CustomReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
        super(delegate);
    }

//    public String getTableIdentifierStrategyName(org.hibernate.cfg.reveng.TableIdentifier tableIdentifier){
//        return "GeneratorType.AUTO";
//    }
}
