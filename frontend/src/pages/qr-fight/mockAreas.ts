import { QrArea } from '../../util/views/qrFight.view'

export const mockAreas: QrArea[] = [
  {
    name: 'Egyetem',
    level: 1,
    id: 'uni',
    unlocked: true,
    status: 'completed',
    teams: [
      { name: 'Chillámák', value: 2 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 },
      { name: 'Schugár', value: 1 }
    ]
  },
  {
    name: 'Schönherz',
    level: 2,
    id: 'sch',
    unlocked: true,
    status: 'current',
    teams: [
      { name: 'Chillámák', value: 10 },
      { name: 'Schugár', value: 5 }
    ]
  },
  {
    name: 'Valami más',
    level: 3,
    id: 'hcs',
    unlocked: false,
    teams: [
      { name: 'Chillámák', value: 4 },
      { name: 'Schugár', value: 3 }
    ]
  }
]
