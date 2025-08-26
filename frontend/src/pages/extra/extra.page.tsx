import { Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useParams } from 'react-router'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useExtraPage } from '../../api/hooks/extra/useExtraPage'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { RoleType, RoleTypeString } from '../../util/views/profile.view'

const ExtraPage = () => {
  const params = useParams()
  const { authInfo } = useAuthContext()
  const { data, isLoading, isError } = useExtraPage(params.slug || '')
  const { sendMessage } = useServiceContext()

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} />

  if (RoleType[data.minRole] > RoleType.GUEST && authInfo && RoleType[authInfo?.role ?? RoleTypeString.GUEST] < RoleType[data.minRole]) {
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
