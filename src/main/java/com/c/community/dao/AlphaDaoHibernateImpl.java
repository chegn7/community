package com.c.community.dao;

import org.springframework.stereotype.Repository;

@Repository("dao1name")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
