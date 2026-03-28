import { LinkButton } from '@/common-components/LinkButton'
import type { ProfileView } from '@/util/views/profile.view'
import { FaFacebook, FaPhone } from 'react-icons/fa'

export const GroupLeaderContactList = ({ profile }: { profile: ProfileView }) => {
  if (!profile?.groupLeaders?.length || profile.groupLeaders.length === 0) return null
  return (
    <div className="mt-5">
      <div className="flex flex-col gap-5 items-start">
        <h2 className="text-xl font-bold">Csoporthoz tartozó elérhetőségek</h2>
        {profile.groupLeaders.map((gl, index) => (
          <div
            key={index}
            className={
              'flex w-full flex-col items-center justify-between gap-3 rounded-md ' +
              'bg-secondary text-secondary-foreground border p-3 lg:flex-row'
            }
          >
            <p>{gl.name}</p>
            <div className="flex flex-col gap-3 md:flex-row">
              {gl.facebookUrl && (
                <LinkButton href={gl.facebookUrl} external newTab>
                  <FaFacebook className="mr-2" />
                  Facebook
                </LinkButton>
              )}
              {gl.mobilePhone && (
                <LinkButton href={'tel:' + gl.mobilePhone} external>
                  <FaPhone className="mr-2" />
                  {gl.mobilePhone}
                </LinkButton>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
