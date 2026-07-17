package dev.cakestudio.aucCoinsEngine;

import dev.cakestudio.aucCoinsEngine.economy.CoinsEngineEconomyProvider;

import dev.cakestudio.cakeauctionapi.addon.AbstractAddon;
import dev.cakestudio.cakeauctionapi.api.ICakeAuctionAPI;
import dev.cakestudio.cakeauctionapi.api.manager.economy.IEconomyManager;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

@Getter
public final class AucCoinsEngine extends AbstractAddon {

    private CoinsEngineEconomyProvider economyProvider;

    @Override
    protected void onEnable() {
        saveDefaultConfig();

        if (Bukkit.getPluginManager().getPlugin("ExcellentEconomy") == null) {
            getLogger().warning("ExcellentEconomy (CoinsEngine) not found! Disabling addon.");
            setEnabled(false);
            return;
        }

        RegisteredServiceProvider<ExcellentEconomyAPI> provider = Bukkit.getServer().getServicesManager().getRegistration(ExcellentEconomyAPI.class);
        if (provider == null) {
            getLogger().warning("ExcellentEconomyAPI not found in ServicesManager! Disabling addon.");
            setEnabled(false);
            return;
        }

        ExcellentEconomyAPI api = provider.getProvider();
        String currencyId = getConfig().getString("currency-id", "coins");

        try {
            this.economyProvider = new CoinsEngineEconomyProvider(api, currencyId);
            
            ICakeAuctionAPI cakeAuctionAPI = getApi();
            IEconomyManager economyManager = cakeAuctionAPI.getEconomyManager();
            economyManager.registerProvider(this.economyProvider);
            
            getLogger().info("Successfully registered CoinsEngine (ExcellentEconomy) economy provider for currency: " + currencyId);
        } catch (Exception e) {
            getLogger().severe("Failed to initialize CoinsEngine economy provider: " + e.getMessage());
            setEnabled(false);
        }
    }

    @Override
    protected void onDisable() {
    }

}