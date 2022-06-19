import { ProfileView } from '../../../util/views/profile.view'

export const collectChallengeDetails = (profile: ProfileView) => [
  {
    name: 'Riddle',
    completed: profile?.completedRiddleCount,
    total: profile?.totalRiddleCount,
    link: '/riddleok',
    percentage: profile?.totalRiddleCount === 0 ? 0 : (profile?.completedRiddleCount / profile?.totalRiddleCount) * 100
  },
  {
    name: 'QR kód',
    completed: profile?.collectedTokenCount,
    total: profile?.totalTokenCount,
    link: '/qr',
    percentage: profile?.totalTokenCount === 0 ? 0 : (profile?.collectedTokenCount / profile?.totalTokenCount) * 100
  }
]
