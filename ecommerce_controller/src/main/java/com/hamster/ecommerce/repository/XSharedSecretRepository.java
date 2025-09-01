package com.hamster.ecommerce.repository;


import com.hamster.ecommerce.model.entity.XSharedSecret;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XSharedSecretRepository extends CrudRepository<XSharedSecret, Long>
{
    @Query(value = "select * from x_shared_secret where id = (select max(id) from x_shared_secret)")
    XSharedSecret getMostRecentSecretKey();

    @Query(value = "select * from x_shared_secret order by create_datetime desc")
    List<XSharedSecret> findAllOrderByCreateDateTimeDesc();

}
