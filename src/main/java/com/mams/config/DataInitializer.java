package com.mams.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mams.entity.Base;
import com.mams.entity.Equipment;
import com.mams.entity.Inventory;
import com.mams.entity.Role;
import com.mams.entity.User;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentRepository;
import com.mams.repository.InventoryRepository;
import com.mams.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(
            BaseRepository bases,
            EquipmentRepository eq,
            InventoryRepository inv,
            UserRepository users,
            PasswordEncoder enc) {

        return args -> {
            Base a = bases.findAll().stream()
                    .filter(x -> x.name.equals("Alpha Base"))
                    .findFirst()
                    .orElseGet(() -> bases.save(new Base("Alpha Base", "Delhi")));

            Base b = bases.findAll().stream()
                    .filter(x -> x.name.equals("Bravo Base"))
                    .findFirst()
                    .orElseGet(() -> bases.save(new Base("Bravo Base", "Pune")));

            Base c = bases.findAll().stream()
                    .filter(x -> x.name.equals("Charlie Base"))
                    .findFirst()
                    .orElseGet(() -> bases.save(new Base("Charlie Base", "Chennai")));

            Equipment v = eq.save(new Equipment("Military Vehicle", "VEHICLE", "units"));
            Equipment w = eq.save(new Equipment("Assault Rifle", "WEAPON", "units"));
            Equipment am = eq.save(new Equipment("Ammunition", "AMMUNITION", "rounds"));
            Equipment r = eq.save(new Equipment("Radio", "COMMUNICATION", "units"));

            if (inv.count() == 0) {
                for (Base x : new Base[]{a, b, c}) {
                    inv.save(new Inventory(x, v, 20, 20));
                    inv.save(new Inventory(x, w, 300, 300));
                    inv.save(new Inventory(x, am, 1000, 1000));
                    inv.save(new Inventory(x, r, 50, 50));
                }
            }

            if (users.count() == 0) {
                users.save(new User("admin", enc.encode("Admin@123"), Role.ADMIN, null));
                users.save(new User("commander", enc.encode("Commander@123"), Role.BASE_COMMANDER, a));
                users.save(new User("logistics", enc.encode("Logistics@123"), Role.LOGISTICS_OFFICER, a));
            }
        };
    }
}