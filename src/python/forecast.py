import pandas as pd
from statsmodels.tsa.arima.model import ARIMA
import sys


def forecast_sales(data):
    series = pd.Series(data)
    model = ARIMA(series, order=(5, 1, 0))
    model_fit = model.fit()
    forecast = model_fit.forecast(steps=1)
    return forecast.iloc[0]


if __name__ == "__main__":
    sales_data = [float(x) for x in sys.argv[1:]]
    forecast = forecast_sales(sales_data)
    print(forecast)
