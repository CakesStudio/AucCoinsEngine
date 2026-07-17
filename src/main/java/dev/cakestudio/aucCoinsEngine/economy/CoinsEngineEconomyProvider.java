package dev.cakestudio.aucCoinsEngine.economy;

import dev.cakestudio.cakeauctionapi.api.manager.economy.IEconomyProvider;

import lombok.NonNull;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;

public class CoinsEngineEconomyProvider implements IEconomyProvider {

    private final ExcellentEconomyAPI api;
    private final String currencyId;
    private final ExcellentCurrency currency;

    public CoinsEngineEconomyProvider(ExcellentEconomyAPI api, String currencyId) {
        this.api = api;
        this.currencyId = currencyId;
        this.currency = api.getCurrency(currencyId);
        if (this.currency == null) {
            throw new IllegalArgumentException("Currency '" + currencyId + "' not found in ExcellentEconomy!");
        }
    }

    @Override
    public @NonNull String getName() {
        return "ExcellentEconomy";
    }

    @Override
    public double getBalance(@NonNull OfflinePlayer player) {
        Player online = player.getPlayer();
        if (online != null) {
            return api.getBalance(online, currency);
        }
        try {
            Double balance = api.getBalanceAsync(player.getUniqueId(), currency).join();
            return balance != null ? balance : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public boolean has(@NonNull OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean withdraw(@NonNull OfflinePlayer player, double amount) {
        Player online = player.getPlayer();
        if (online != null) {
            return api.withdraw(online, currency, amount);
        }
        try {
            var result = api.withdrawAsync(player.getUniqueId(), currency, amount).join();
            return result != null && result.success();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deposit(@NonNull OfflinePlayer player, double amount) {
        Player online = player.getPlayer();
        if (online != null) {
            return api.deposit(online, currency, amount);
        }
        try {
            var result = api.depositAsync(player.getUniqueId(), currency, amount).join();
            return result != null && result.success();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public @NonNull String format(double amount) {
        return currency.format(amount);
    }

}