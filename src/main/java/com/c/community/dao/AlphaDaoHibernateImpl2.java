package com.c.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphaDaoHibernateImpl2 implements AlphaDao {
    @Override
    public String select() {
        return "new Hibernate";
    }
}
