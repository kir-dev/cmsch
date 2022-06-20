import { ProfileView } from '../../../util/views/profile.view'

export const submittedPercent = (profile: ProfileView) => {
  let res = (profile.submittedTaskCount / profile.totalTaskCount) * 100
  return isNaN(res) ? 0 : res
}

export const completedPercent = (profile: ProfileView) => {
  let res = (profile.completedTaskCount / profile.totalTaskCount) * 100
  return isNaN(res) ? 0 : res
}
