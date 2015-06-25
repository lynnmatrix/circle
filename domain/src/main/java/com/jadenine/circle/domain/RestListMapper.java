package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;
import com.jadenine.circle.model.rest.JSONListWrapper;

import java.util.List;

import rx.functions.Func1;

/**
 * Created by linym on 6/18/15.
 */
class RestListMapper<E extends Savable, D extends Updatable>  implements
        Func1<JSONListWrapper<E>, List<D>>{
    private final RestMapper restMapper;

    public RestListMapper(MapperDelegate<E, D> mapperDelegate, List<D> origin){
        restMapper = new RestMapper(mapperDelegate, origin);
    }

    @Override
    public List<D> call(JSONListWrapper<E> entities) {
        for (E entity : entities.getAll()) {
            restMapper.call(entity);
        }
        restMapper.getMapperDelegate().setHasMore(entities.hasMore());

        return restMapper.getOrigin();
    }
}
