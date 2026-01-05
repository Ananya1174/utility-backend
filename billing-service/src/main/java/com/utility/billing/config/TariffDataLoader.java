package com.utility.billing.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.utility.billing.model.TariffPlan;
import com.utility.billing.model.TariffSlab;
import com.utility.billing.repository.TariffPlanRepository;
import com.utility.billing.repository.TariffSlabRepository;
import com.utility.billing.model.UtilityType;

@Configuration
public class TariffDataLoader {

	@Bean
	CommandLineRunner loadTariffData(TariffPlanRepository planRepo, TariffSlabRepository slabRepo) {

		return args -> {

			if (planRepo.count() > 0) {
				System.out.println("Tariff data already exists. Skipping seed.");
				return;
			}

			List<TariffPlan> electricityPlans = new ArrayList<>();
			electricityPlans.add(new TariffPlan(UtilityType.ELECTRICITY, "DOMESTIC", true));
			electricityPlans.add(new TariffPlan(UtilityType.ELECTRICITY, "COMMERCIAL", true));
			electricityPlans.add(new TariffPlan(UtilityType.ELECTRICITY, "INDUSTRIAL", true));
			planRepo.saveAll(electricityPlans);

			List<TariffSlab> electricitySlabs = new ArrayList<>();
			electricitySlabs.add(new TariffSlab(UtilityType.ELECTRICITY, "DOMESTIC", 0, 100, 2.5));
			electricitySlabs.add(new TariffSlab(UtilityType.ELECTRICITY, "DOMESTIC", 101, 200, 3.5));
			electricitySlabs.add(new TariffSlab(UtilityType.ELECTRICITY, "DOMESTIC", 201, 300, 4.5));
			electricitySlabs.add(new TariffSlab(UtilityType.ELECTRICITY, "DOMESTIC", 301, 100000, 5.0));

			electricitySlabs.add(new TariffSlab(UtilityType.ELECTRICITY, "COMMERCIAL", 0, 100, 4.0));
			electricitySlabs.add(new TariffSlab(UtilityType.ELECTRICITY, "COMMERCIAL", 101, 300, 5.5));
			electricitySlabs.add(new TariffSlab(UtilityType.ELECTRICITY, "COMMERCIAL", 301, 100000, 7.0));

			electricitySlabs.add(new TariffSlab(UtilityType.ELECTRICITY, "INDUSTRIAL", 0, 500, 6.0));
			electricitySlabs.add(new TariffSlab(UtilityType.ELECTRICITY, "INDUSTRIAL", 501, 100000, 8.0));
			slabRepo.saveAll(electricitySlabs);

			List<TariffPlan> waterPlans = new ArrayList<>();
			waterPlans.add(new TariffPlan(UtilityType.WATER, "RESIDENTIAL", true));
			waterPlans.add(new TariffPlan(UtilityType.WATER, "BULK", true));
			planRepo.saveAll(waterPlans);

			List<TariffSlab> waterSlabs = new ArrayList<>();
			waterSlabs.add(new TariffSlab(UtilityType.WATER, "RESIDENTIAL", 0, 15, 10));
			waterSlabs.add(new TariffSlab(UtilityType.WATER, "RESIDENTIAL", 16, 30, 20));
			waterSlabs.add(new TariffSlab(UtilityType.WATER, "RESIDENTIAL", 31, 100000, 30));
			waterSlabs.add(new TariffSlab(UtilityType.WATER, "BULK", 0, 50, 25));
			waterSlabs.add(new TariffSlab(UtilityType.WATER, "BULK", 51, 100000, 40));
			slabRepo.saveAll(waterSlabs);
			
			List<TariffPlan> gasPlans = new ArrayList<>();
			gasPlans.add(new TariffPlan(UtilityType.GAS, "PNG_DOMESTIC", true));
			gasPlans.add(new TariffPlan(UtilityType.GAS, "PNG_COMMERCIAL", true));
			planRepo.saveAll(gasPlans);

			List<TariffSlab> gasSlabs = new ArrayList<>();
			gasSlabs.add(new TariffSlab(UtilityType.GAS, "PNG_DOMESTIC", 0, 20, 35));
			gasSlabs.add(new TariffSlab(UtilityType.GAS, "PNG_DOMESTIC", 21, 100000, 45));
			gasSlabs.add(new TariffSlab(UtilityType.GAS, "PNG_COMMERCIAL", 0, 50, 50));
			gasSlabs.add(new TariffSlab(UtilityType.GAS, "PNG_COMMERCIAL", 51, 100000, 70));
			slabRepo.saveAll(gasSlabs);

			List<TariffPlan> internetPlans = new ArrayList<>();
			internetPlans.add(new TariffPlan(UtilityType.INTERNET, "BASIC_50MBPS", true));
			internetPlans.add(new TariffPlan(UtilityType.INTERNET, "PREMIUM_100MBPS", true));
			internetPlans.add(new TariffPlan(UtilityType.INTERNET, "ULTRA_200MBPS", true));
			planRepo.saveAll(internetPlans);

			List<TariffSlab> internetSlabs = new ArrayList<>();
			internetSlabs.add(new TariffSlab(UtilityType.INTERNET, "BASIC_50MBPS", 0, 100000, 499));
			internetSlabs.add(new TariffSlab(UtilityType.INTERNET, "PREMIUM_100MBPS", 0, 100000, 799));
			internetSlabs.add(new TariffSlab(UtilityType.INTERNET, "ULTRA_200MBPS", 0, 100000, 1199));
			slabRepo.saveAll(internetSlabs);

		};
	}
}