import { TeamView } from '../../util/views/team.view'

export const TeamMock: TeamView[] = [
  {
    name: 'Chillámák',
    id: '1',
    points: 30,
    members: [
      { name: 'Berente Bálint', id: '123456', isAdmin: true },
      { name: 'Berente Bálint 2', id: '12345', isAdmin: false }
    ],
    applicants: [{ name: 'Berente Bálint', id: '123456', isAdmin: false }]
  },
  {
    name: 'Schugár',
    id: '2',
    points: 12,
    members: [{ name: 'Berente Bálint', id: '123456', isAdmin: true }],
    applicants: [{ name: 'Berente Bálint', id: '123456', isAdmin: false }]
  }
]
