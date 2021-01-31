package org.app.repository;

import org.app.entity.Plate;
import org.app.projections.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlateRepository extends JpaRepository<Plate, Integer> {

    Page<Plate> findAllByInStorageFalseAndCrashFalse(Pageable pageable); // передали

    Page<Plate> findAllByInStorageTrueAndCrashFalse(Pageable pageable); // сейчас на складе

    List<Plate> findAllByInStorageTrueAndCrashFalse(); // сейчас на складе

    Page<Plate> findAllByCrashTrue(Pageable pageable); // в изоляторе брака

    Integer countAllByInStorageTrue(); // кол-во всех заготовок на скаладе

    Integer countAllByInStorageTrueAndHasCrackFalse(); // кол-во заготовок группы 1 (без свилей)

    Integer countAllByInStorageTrueAndHasCrackTrue(); // кол-во заготовок группы 2 (со свилями)


    @Query(value =
            "SELECT " +
                "m.name AS materialName,  " +
                "t.name AS typeName, " +
                "count(t.name) AS countPlates " +
            "FROM plates p " +
            "JOIN types t ON  t.id = p.types_id " +
            "JOIN materials m ON m.id = p.materials_id " +
            "WHERE p.in_storage = true " +
            "GROUP BY m.name, t.name " +
            "ORDER BY m.name, t.name;" ,
            nativeQuery = true)
    List<PlateMaterialTypeCountProjections> findAllPlateMaterialAndType(); // все заготовки на складе с разбиением по типу и материалу

    @Query(value =
            "SELECT " +
                "prod.name AS producerName, " +
                "m.name AS materialName, " +
                "t.name AS typeName, " +
            "count(t.name) AS countPlates " +
            "FROM plates p " +
            "JOIN types t ON t.id = p.types_id " +
            "JOIN materials m ON m.id = p.materials_id " +
            "JOIN protocols prot ON prot.id = p.protocol_id " +
            "JOIN producers prod ON prod.id = prot.producer_id " +
            "WHERE month(prot.date) = month(now()) " +
            "AND p.in_storage = false " +
            "GROUP BY m.name, t.name, prod.name " +
            "ORDER BY producerName, materialName, typeName;" ,
            nativeQuery = true)
    List<PlateToProducersProjection> findAllActivePlateByProducerForThisMonth(); // все переданные заготовки с разбиением по типу, материалу, производителю за текущий месяц

    @Query(value =
            "SELECT " +
                "m.name AS materialName, " +
                "t.name As typeName, " +
                "count(t.name) AS countPlates " +
            "FROM plates p " +
            "JOIN types t ON  t.id = p.types_id " +
            "JOIN materials m ON m.id = p.materials_id " +
            "WHERE RIGHT(p.number, 5) LIKE CONCAT('%', DATE_FORMAT(NOW(), '%m.%y')) " +
            "GROUP BY materialName, typeName " +
            "ORDER BY materialName, typeName;" ,
            nativeQuery = true)
    List<PlateControllingThisMonthProjection> findAllControllingThisMonth(); // все заготовки, проконтролированные в этом месяце

    List<Plate> findAllByNumberContainingOrderByNumber(String numberRegex); // все заготовки по части номера

    List<Plate> findAllByInStorageTrueAndCrashFalseAndNumberContainingOrderByNumber(String numberRegex); // все, имеющиеся заготовки по части номера

    // все заготовки на складе с разбиением по типу и материалу по ГРУППАМ
    @Query(value =
            "SELECT " +
                "m.name AS materialName, " +
                "t.name AS typeName, " +
                "p.has_crack AS groupName, " +
            "count(t.name) AS countPlates " +
            "FROM plates p " +
            "JOIN types t ON  t.id = p.types_id " +
            "JOIN materials m ON m.id = p.materials_id " +
            "WHERE p.in_storage = true " +
            "GROUP BY materialName, typeName, groupName " +
            "ORDER BY materialName, typeName, groupName;" ,
            nativeQuery = true)
    List<PlateMaterialTypeCountGroupProjections> findAllPlateMaterialAndTypeAndGroup();

    // все переданные заготовки с разбиением по типу, материалу, производителю за текущий месяц по ГРУППАМ
    @Query(value =
            "SELECT " +
                "prod.name AS producerName, " +
                "m.name AS materialName, " +
                "t.name AS typeName, " +
                "p.has_crack AS groupName, " +
            "count(t.name) AS countPlates " +
            "FROM plates p " +
            "JOIN types t ON t.id = p.types_id " +
            "JOIN materials m ON m.id = p.materials_id " +
            "JOIN protocols prot ON prot.id = p.protocol_id " +
            "JOIN producers prod ON prod.id = prot.producer_id " +
            "WHERE month(prot.date) = month(now()) " +
            "AND p.in_storage = false " +
            "GROUP BY materialName, typeName, producerName, groupName " +
            "ORDER BY producerName, materialName, typeName, groupName;" ,
            nativeQuery = true)
    List<PlateToProducersGroupProjection> findAllActivePlateByProducerForThisMonthToGroup();

    // все заготовки, проконтролированные в этом месяце по ГРУППАМ
    @Query(value =
            "SELECT " +
                "m.name AS materialName, " +
                "t.name As typeName, " +
                "p.has_crack AS groupName, " +
                "count(t.name) AS countPlates " +
            "FROM plates p " +
            "JOIN types t ON  t.id = p.types_id " +
            "JOIN materials m ON m.id = p.materials_id " +
            "WHERE RIGHT(p.number, 5) LIKE CONCAT('%', DATE_FORMAT(NOW(), '%m.%y')) " +
            "GROUP BY materialName, typeName, groupName " +
            "ORDER BY materialName, typeName, groupName;" ,
            nativeQuery = true)
    List<PlateControllingThisMonthGroupProjection> findAllControllingThisMonthToGroup();

    Plate findByNumber(String number);
}
