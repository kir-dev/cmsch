import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useRiddleListQuery } from '@/api/hooks/riddle/useRiddleListQuery.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable.tsx'
import { CustomBreadcrumb } from '@/common-components/CustomBreadcrumb.tsx'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { PageStatus } from '@/common-components/PageStatus'
import { Button } from '@/components/ui/button'
import { AbsolutePaths } from '@/util/paths'
import { Link, useNavigate, useParams } from 'react-router'
import { RiddleListItem } from './components/RiddleListItem.tsx'

const RiddleCategoryPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const component = useConfigContext()?.components?.riddle
  const { isLoading, isError, data } = useRiddleListQuery()

  if (!component) return <ComponentUnavailable />

  const category = data?.find((category) => category.categoryId === Number(id))
  if (isError || isLoading || !category) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const riddles = category.nextRiddles
  const breadcrumbItems = [
    {
      title: 'Riddle',
      to: AbsolutePaths.RIDDLE
    },
    {
      title: category.title
    }
  ]
  return (
    <CmschPage title={component?.title}>
      <CustomBreadcrumb items={breadcrumbItems} />
      <div className="flex flex-col justify-between md:flex-row md:items-end">
        <h1 className="my-5 text-4xl font-bold tracking-tight">Riddleök | {category.title}</h1>
        <Button asChild>
          <Link to={AbsolutePaths.RIDDLE_HISTORY}>Megoldott riddleök</Link>
        </Button>
      </div>
      <div className="mt-5 flex flex-col gap-4">
        {(riddles ?? []).length > 0 ? (
          riddles.map((riddle) => (
            <RiddleListItem riddle={riddle} key={riddle.id} onClick={() => navigate(`${AbsolutePaths.RIDDLE}/solve/${riddle.id}`)} />
          ))
        ) : (
          <p>Nincs egyetlen megoldásra váró riddle feladat sem. Szép munka!</p>
        )}
      </div>
    </CmschPage>
  )
}

export default RiddleCategoryPage
