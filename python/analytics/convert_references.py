import pandas as pd
import os




root_dp = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP"
reference_rdp = 'data/reference'
ref_fn = 'yanasse_E_reference.csv'
ref_out_fn = 'reference.csv'
prefix = 'yan'
df = pd.read_csv(os.path.join(root_dp,reference_rdp,ref_fn))

# df.insert(loc=4, column='Best_ILS', value=pd.NA)
# df.insert(loc=5, column='Avg_ILS', value=pd.NA)
# df.insert(loc=6, column='T_ILS', value=pd.NA)
# df.insert(loc=7, column='Best_DQGA', value=pd.NA)
# df.insert(loc=8, column='Avg_DQGA', value=pd.NA)
# df.insert(loc=8, column='T_DQGA', value=pd.NA)


df.columns = ['n',
              'm',
              'C',
              'i',
              'Best_ILS',
              'Avg_ILS',
              'T_ILS',
              'Best_DQGA',
              'Avg_DQGA',
              'T_DQGA',
              'Best_HGS',
              'Avg_HGS',
              'T_HGS'
              ]

df.insert(loc=0, column='author', value=prefix)

#df
df.to_csv(os.path.join(root_dp,reference_rdp,ref_out_fn), mode='a', header=False, index=False)

