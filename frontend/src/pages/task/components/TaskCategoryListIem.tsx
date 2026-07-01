import { CircularProgress } from '@/common-components/CircularProgress'
import { AbsolutePaths } from '@/util/paths'
import type { TaskCategoryPreview } from '@/util/views/task.view'
import { Link } from 'react-router'

export const TaskCategoryListItem = ({ category }: { category: TaskCategoryPreview }) => {
  const percentage = (category.sum > 0 ? (category.approved + category.notGraded) / category.sum : 0) * 100

  return (
    <div className="rounded-md bg-secondary text-secondary-foreground px-6 py-2 transition-colors hover:bg-secondary/80 border">
      <Link to={`${AbsolutePaths.TASKS}/category/${category.categoryId}`}>
        <div className="flex items-center justify-between">
          <span className="text-xl font-bold">{category.name}</span>
          <div className="flex items-center gap-2">
            <span className="font-bold">
              {category.approved + category.notGraded}/{category.sum}
            </span>
            <CircularProgress
              value={percentage}
              size={40}
              strokeWidth={4}
              label=""
              color={category.notGraded > 0 ? 'text-warning' : 'text-success'}
            />
          </div>
        </div>
      </Link>
    </div>
  )
}
