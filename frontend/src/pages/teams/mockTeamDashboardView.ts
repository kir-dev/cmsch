import { taskCategoryType } from '../../util/views/task.view'
import { TeamDashboardView } from '../../util/views/team.view'

export const MockTeamDashboardView: TeamDashboardView = {
  memberCount: 10,
  points: 10,
  leaderboardPlace: 10,
  teamName: 'Test Team',
  bannerUrl: 'https://api.g7.sch.bme.hu/cdn/public/BEKOLTOZES_T3_M0MLO6BIYY7I.jpg',
  creatorName: 'Lórum Ipse',
  teamId: '6',
  riddles: {
    maxCount: 100,
    solvedCount: 60
  },
  task: {
    categories: [
      {
        categoryId: 1,
        name: 'Test Category',
        approved: 10,
        notGraded: 40,
        rejected: 10,
        sum: 100,
        type: taskCategoryType.REGULAR
      },
      {
        categoryId: 2,
        name: 'Test Category',
        approved: 10,
        notGraded: 0,
        rejected: 10,
        sum: 100,
        type: taskCategoryType.REGULAR
      }
    ]
  }
}
