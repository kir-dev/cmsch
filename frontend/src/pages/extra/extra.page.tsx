import { FunctionComponent } from 'react'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Heading } from '@chakra-ui/react'
import Markdown from '../../common-components/Markdown'
import { Navigate, useParams } from 'react-router'
import { useExtraPage } from '../../api/hooks/extra/useExtraPage'
import { Helmet } from 'react-helmet-async'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { RoleType, RoleTypeString } from '../../util/views/profile.view'
import { AbsolutePaths } from '../../util/paths'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { l } from '../../util/language'
import { PageStatus } from '../../common-components/PageStatus'

interface ExtraPageProps {}

const ExtraPage: FunctionComponent<ExtraPageProps> = () => {
  const params = useParams()
  const { authInfo } = useAuthContext()
  const { data, isLoading, isError } = useExtraPage(params.slug || '')
  const { sendMessage } = useServiceContext()

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} />

  if (RoleType[data.minRole] > RoleType.GUEST && authInfo && RoleType[authInfo?.role ?? RoleTypeString.BASIC] < RoleType[data.minRole]) {
    sendMessage(l('no-permission'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }
  return (
    <CmschPage>
      <Helmet title={data.title} />
      <Heading as="h1" variant="main-title">
        {data.title}
      </Heading>
      <Markdown text={data.content} />
    </CmschPage>
  )
}

export default ExtraPage
