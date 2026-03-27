package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.lobby.stock.StockData;
import dabbiks.uhc.tasks.Task;

public class StockTask extends Task {

    public static double stockChange = 0.0;
    private final StockData stockData;

    public StockTask(StockData stockData) {
        this.stockData = stockData;
    }

    @Override
    protected long getPeriod() {
        return 1200;
    }

    @Override
    protected void tick() {
        if (stockChange != 0.0) {
            stockData.setCurrentPrice(stockData.getCurrentPrice() + stockChange);
            stockChange = 0.0;
        }
        stockData.buildChart();
    }
}