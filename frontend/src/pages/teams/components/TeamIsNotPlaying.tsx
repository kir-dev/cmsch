import { CmschPage } from '@/common-components/layout/CmschPage'

export function TeamIsNotPlaying() {
  return (
    <CmschPage>
      <div className="flex flex-col items-center text-center">
        <div>
          <h2 className="text-2xl font-bold mb-2">A csapat nem látható</h2>
          <p>Ez a csapat nincs játékban, vagy el van rejtve.</p>
        </div>
      </div>
    </CmschPage>
  )
}
