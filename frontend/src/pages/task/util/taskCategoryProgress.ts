import { TaskCategoryPreview } from '../../../util/views/task.view'

export const progress = (category: TaskCategoryPreview) => (category.approved + category.notGraded) / category.sum

export const progressGradient = (progress: number, color: string) => {
  const endDeg = 360 * progress
  if (progress === 1) {
    return `conic-gradient(${color} 0deg, ${color} 360deg)`
  }
  if (progress === 0) {
    return `conic-gradient(white 0deg, white 360deg)`
  }
  return `conic-gradient(white 0deg,${color} 10deg, ${color} ${endDeg}deg, white ${endDeg + 10}deg)`
}
