import { AbsolutePaths } from '../../../util/paths'
import { ProfileView } from '../../../util/views/profile.view'

export const collectChallengeDetails = (profile: ProfileView) => [
  {
    name: 'Riddle',
    completed: profile?.completedRiddleCount,
    total: profile?.totalRiddleCount,
    link: AbsolutePaths.RIDDLE,
    percentage: profile?.totalRiddleCount === 0 ? 0 : (profile?.completedRiddleCount / profile?.totalRiddleCount) * 100
  },
  {
    name: 'QR kód',
    completed: profile?.collectedTokenCount,
    total: profile?.totalTokenCount,
    link: AbsolutePaths.TOKEN,
    percentage: profile?.totalTokenCount === 0 ? 0 : (profile?.collectedTokenCount / profile?.totalTokenCount) * 100
  }
]
