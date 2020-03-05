
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go


df = pd.read_csv('/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/log.csv')

#fig = px.line(df, x = 'Time', y = 'Accepted', z='Temperature', title='SSP')
fig = px.line_3d(df, x = 'Time', y = 'Temperature', z='Accepted', title='SSP', template="plotly_dark")


#fig = go.Figure(data=[go.Surface(z=df.Accepted, x=df.Time, y=df.Temperature)])
fig.show()


