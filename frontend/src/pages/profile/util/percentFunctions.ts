import { ProfileView } from '../../../util/views/profile.view'

export const submittedPercent = (profile: ProfileView) => {
  let res = (profile.submittedAchievementCount / profile.totalAchievementCount) * 100
  return isNaN(res) ? 0 : res
}

export const completedPercent = (profile: ProfileView) => {
  let res = (profile.completedAchievementCount / profile.totalAchievementCount) * 100
  return isNaN(res) ? 0 : res
}
