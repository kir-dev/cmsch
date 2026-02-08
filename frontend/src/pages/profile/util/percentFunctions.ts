import type { ProfileView } from '../../../util/views/profile.view'

const percent = (dividend: number, divisor: number) => (divisor != 0 ? (dividend / divisor) * 100 : 0)

export const submittedPercent = (profile: ProfileView) => percent(profile?.submittedTaskCount || 0, profile.totalTaskCount || 0)

export const completedPercent = (profile: ProfileView) => percent(profile?.completedTaskCount || 0, profile.totalTaskCount || 0)
