import type { Dev } from '@/api/hooks/developers/useDevelopers'
import { Badge } from '@/components/ui/badge'
import { API_BASE_URL } from '@/util/configs/environment.config'

export type Props = {
  dev: Dev
}

export const DeveloperWrapItem = ({ dev: { name, img, tags } }: Props) => {
  return (
    <div className="flex flex-col items-center w-80 h-80">
      <span className="text-2xl">{name}</span>
      <img
        src={img}
        className="h-60 object-contain"
        alt={name}
        onError={(e) => (e.currentTarget.src = `${API_BASE_URL}/img/big_pear_logo.png`)}
      />
      <div className="flex flex-row space-x-2 my-2">
        {tags.map((tag) => (
          <Badge variant="default" className="font-bold" key={tag}>
            {tag}
          </Badge>
        ))}
      </div>
    </div>
  )
}
