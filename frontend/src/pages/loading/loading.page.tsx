import { CmschPage } from '@/common-components/layout/CmschPage'
import { Loading } from '@/common-components/Loading'

export function LoadingPage() {
  return (
    <Loading>
      <CmschPage title="Betöltés" className="w-fit">
        <div className="flex items-center justify-center">
          <Loading />
        </div>
      </CmschPage>
    </Loading>
  )
}
