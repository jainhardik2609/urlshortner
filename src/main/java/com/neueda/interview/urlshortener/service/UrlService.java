package com.neueda.interview.urlshortener.service;

import com.neueda.interview.urlshortener.repository.UrlRepository;
import com.neueda.interview.urlshortener.common.ShorteningUtil;
import com.neueda.interview.urlshortener.model.UrlEntity;
import com.neueda.interview.urlshortener.dto.FullUrl;
import com.neueda.interview.urlshortener.dto.ShortUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    Logger logger = LoggerFactory.getLogger(UrlService.class);

    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    private UrlEntity get(Long id) {
        logger.info(String.format("Fetching Url from database for Id %d", id));
        UrlEntity urlEntity = urlRepository.findById(id).get();
        return urlEntity;
    }

    /**
     * Uses the Base62 encoded to convert to Base10 number and fetches the corresponding record from the database
     *
     * @param shortenString Base62 encoded string
     * @return FullUrl object
     */
    public FullUrl getFullUrl(String shortenString) {
        logger.debug("Converting Base 62 string %s to Base 10 id");
        Long id = ShorteningUtil.strToId(shortenString);
        logger.info(String.format("Converted Base 62 string %s to Base 10 id %s", shortenString, id));

        logger.info(String.format("Retrieving full url for %d", id));
        return new FullUrl(this.get(id).getFullUrl());
    }

    private UrlEntity save(FullUrl fullUrl) {
        return urlRepository.save(new UrlEntity(fullUrl.getFullUrl()));
    }

    /**
     * It saves the full url to database and uses the autogenerated id to convert to Base62 string
     *
     * @param fullUrl FullUrl object to be converted to shortened url
     * @return ShortUrl object
     */
    public ShortUrl getShortUrl(FullUrl fullUrl) {
        logger.info(String.format("Saving Url %s to database", fullUrl.getFullUrl()));
        UrlEntity savedUrl = this.save(fullUrl);
        logger.debug(savedUrl.toString());

        logger.debug(String.format("Converting Base 10 %d to Base 62 string", savedUrl.getId()));
        String shortUrlText = ShorteningUtil.idToStr(savedUrl.getId());
        logger.info(String.format("Converted Base 10 %d to Base 62 string %s", savedUrl.getId(), shortUrlText));

        return new ShortUrl(shortUrlText);
    }
}
