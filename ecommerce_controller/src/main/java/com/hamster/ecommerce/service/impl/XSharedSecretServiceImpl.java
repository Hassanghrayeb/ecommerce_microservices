package com.hamster.ecommerce.service.impl;


import com.hamster.ecommerce.model.entity.XSharedSecret;
import com.hamster.ecommerce.repository.XSharedSecretRepository;
import com.hamster.ecommerce.service.XSharedSecretService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class XSharedSecretServiceImpl implements XSharedSecretService
{
    private final XSharedSecretRepository xSharedSecretRepository;

    public XSharedSecretServiceImpl(XSharedSecretRepository xSharedSecretRepository)
    {
        this.xSharedSecretRepository = xSharedSecretRepository;
    }

    @Override
    public XSharedSecret save(XSharedSecret xSharedSecret)
    {
        return xSharedSecretRepository.save(xSharedSecret);
    }

    @Override
    public XSharedSecret getMostRecentSharedSecret()
    {
        return xSharedSecretRepository.getMostRecentSecretKey();
    }

    @Override
    public List<XSharedSecret> findAllXSharedSecretOrderByCreateDateTimeDesc()
    {
        return xSharedSecretRepository.findAllOrderByCreateDateTimeDesc();
    }

}
