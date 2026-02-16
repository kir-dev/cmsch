import { Navigate } from 'react-router'
import { useTinderAnswers } from '../../api/hooks/community/useTinderAnswers.ts'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { AbsolutePaths } from '../../util/paths.ts'

const TinderRouter = () => {
  const { data, isLoading, isError } = useTinderAnswers()

  if (isLoading || isError || !data) return <PageStatus isLoading={isLoading} isError={isError} />

  if (data.answered) {
    return <Navigate to={`${AbsolutePaths.TINDER}/community`} />
  } else {
    return <Navigate to={`${AbsolutePaths.TINDER}/question`} />
  }
}

export default TinderRouter
