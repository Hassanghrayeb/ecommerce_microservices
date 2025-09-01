package com.hamster.ecommerce.service;


import com.hamster.ecommerce.model.entity.XSharedSecret;

import java.util.List;

public interface XSharedSecretService
{
    XSharedSecret save(XSharedSecret xSharedSecret);

    XSharedSecret getMostRecentSharedSecret();

    List<XSharedSecret> findAllXSharedSecretOrderByCreateDateTimeDesc();
}
