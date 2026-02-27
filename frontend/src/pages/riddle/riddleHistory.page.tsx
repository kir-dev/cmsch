import { useRiddleHistoryQuery } from '@/api/hooks/riddle/useRiddleHistoryQuery'
import { CustomBreadcrumb } from '@/common-components/CustomBreadcrumb'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import { Loading } from '@/common-components/Loading'
import Markdown from '@/common-components/Markdown'
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useToast } from '@/hooks/use-toast'
import { l } from '@/util/language'
import { AbsolutePaths } from '@/util/paths'
import { useEffect, useState } from 'react'
import { FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import { useNavigate } from 'react-router'
import { SpoilerText } from './components/SpoilerText'

const RiddleHistoryPage = () => {
  const { toast } = useToast()
  const navigate = useNavigate()
  const [category, setCategory] = useState('')
  const [loaded, setLoaded] = useState(false)
  const [index, setIndex] = useState(0)

  const query = useRiddleHistoryQuery()
  useEffect(() => {
    if (!loaded && query.isSuccess) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setLoaded(true)
      if (query.data.length > 0) {
        setCategory(query.data![0].categoryName)
      }
    }
  }, [loaded, query.data, query.isSuccess])

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setIndex(0)
  }, [category])

  useEffect(() => {
    if (query.isError) {
      toast({ title: l('riddle-history-query-failed'), variant: 'destructive' })
      navigate(AbsolutePaths.RIDDLE)
    }
  }, [navigate, query.isError, toast])

  if (query.isLoading || !query.data) {
    return <Loading />
  }
  const riddleList = query.data.find((c) => c.categoryName === category)?.submissions
  const riddle = riddleList ? riddleList[index] : undefined
  const breadcrumbItems = [
    {
      title: 'Riddle',
      to: AbsolutePaths.RIDDLE
    },
    {
      title: 'Megoldott riddleök'
    }
  ]
  return (
    <CmschPage title="Megoldott riddleök">
      <CustomBreadcrumb items={breadcrumbItems} />
      <div className="flex flex-col justify-between md:flex-row md:items-center">
        <h1 className="my-5 text-4xl font-bold tracking-tight">Megoldott riddleök</h1>
        <div className="flex flex-col gap-2">
          <Select value={category} onValueChange={(val) => setCategory(val)}>
            <SelectTrigger className="w-full md:w-[20rem]">
              <SelectValue placeholder="Kategória választása" />
            </SelectTrigger>
            <SelectContent>
              {query.data!.map((c) => (
                <SelectItem value={c.categoryName} key={c.categoryName}>
                  {c.categoryName} ({c?.submissions?.length} megoldott riddle)
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          {riddleList && riddle && (
            <Select value={String(index)} onValueChange={(val) => setIndex(parseInt(val))}>
              <SelectTrigger className="w-full md:w-[20rem]">
                <SelectValue placeholder="Riddle választása" />
              </SelectTrigger>
              <SelectContent>
                {riddleList?.map((r, idx) => (
                  <SelectItem value={String(idx)} key={idx}>
                    {r.title}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          )}
        </div>
      </div>
      {!riddle || !riddleList ? (
        <>
          <p className="mt-2">Ebben a kategóriában még nincsenek megoldott riddleök.</p>
          <div className="mt-3 flex justify-center">
            <LinkButton href={AbsolutePaths.RIDDLE}>Összes riddle</LinkButton>
          </div>
        </>
      ) : (
        <>
          <div className="mx-auto w-full max-w-[30rem]">
            <h2 className="my-5 text-2xl font-bold">{riddle.title}</h2>
            {riddle.imageUrl && <img className="w-full rounded-md" src={riddle.imageUrl} alt="Riddle Kép" />}

            <div className="mt-5 flex flex-col items-start gap-1">
              <p>Sorszám: {index + 1}</p>
              <p>Létrehozó: {riddle.creator || 'Nincs megadva'}</p>
              <p>Első megoldó: {riddle.firstSolver || 'Nincs megadva'}</p>
              {riddle.description && <Markdown text={riddle.description} />}
              <p>
                Megoldás (kattintásra jelenik meg): <SpoilerText key={riddle.solution} text={riddle.solution} />
              </p>
              <p>
                Hint{riddle.hint && ' (kattintásra jelenik meg)'}:{' '}
                {riddle.hint ? <SpoilerText key={riddle.solution} text={riddle.hint} /> : 'Nem lett felhasználva'}
              </p>
            </div>

            <div className="my-3 flex justify-between">
              <Button onClick={() => setIndex(index - 1)} disabled={index === 0}>
                <FaArrowLeft className="mr-2" />
                Előző
              </Button>
              <Button onClick={() => setIndex(index + 1)} disabled={index === riddleList?.length - 1}>
                Következő
                <FaArrowRight className="ml-2" />
              </Button>
            </div>
            <div className="flex justify-center">
              <LinkButton href={AbsolutePaths.RIDDLE}>Összes riddle</LinkButton>
            </div>
          </div>
        </>
      )}
    </CmschPage>
  )
}

export default RiddleHistoryPage
