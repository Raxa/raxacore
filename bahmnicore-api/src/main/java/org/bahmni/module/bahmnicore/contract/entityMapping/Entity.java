package org.bahmni.module.bahmnicore.contract.entityMapping;

import lombok.Data;

import java.util.List;

@Data
public class Entity <T1,T2> {
    public T1 entity1;
    public List<T2> mappings;

    public Entity () {
    }

    public Entity(T1 entity1, List<T2> mappings) {
        this.entity1 = entity1;
        this.mappings = mappings;
    }
}
