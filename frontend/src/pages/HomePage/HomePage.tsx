import { Button } from 'antd'
import { Emoji } from 'emoji-picker-react'
import { CalendarCheck, HeartPulse, NotebookPen, PawPrint } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { AnimatedEmoji } from './AnimatedEmoji'
import styles from './HomePage.module.scss'

export default function HomePage() {
	const navigate = useNavigate()
	// Анимации

	return (
		<div className={styles.homeContainer}>
			{/* Герой-секция */}
			<section className={styles.heroSection}>
				<div className={styles.heroContent}>
					<h1>
						<span className={styles.highlight}>PetHealth</span> - ваш надёжный
						помощник в заботе о питомцах <Emoji unified='1f415' size={24} />{' '}
						<Emoji unified='1f408' size={24} />
					</h1>
					<p className={styles.subtitle}>
						Храните всю медицинскую информацию, записи и важные моменты в одном
						удобном месте
					</p>
					<div className={styles.ctaButtons}>
						<Button
							type='primary'
							size='large'
							onClick={() => navigate('/auth/register')}
							className={styles.primaryButton}
						>
							Начать сейчас
						</Button>
						<Button
							size='large'
							onClick={() => navigate('/auth/login')}
							className={styles.secondaryButton}
						>
							Войти в аккаунт
						</Button>
					</div>
				</div>
				<div className={styles.heroIllustration}>
					<div className={styles.petAnimation}>
						<AnimatedEmoji
							emoji='1f43e'
							animation='pulse'
							size={64}
							style={{ margin: '20px 0' }}
						/>
					</div>
				</div>
			</section>

			{/* Преимущества */}
			<section className={styles.featuresSection}>
				<h2>
					Почему выбирают нас? <span className={styles.emoji}>✨</span>
				</h2>
				<div className={styles.featuresGrid}>
					<div className={styles.featureCard}>
						<div className={styles.featureIcon}>
							<PawPrint size={40} color='#3182ce' />
						</div>
						<h3>Профили питомцев</h3>
						<p>
							Создавайте подробные профили для каждого питомца с фото,
							медицинскими данными и особенностями
						</p>
					</div>

					<div className={styles.featureCard}>
						<div className={styles.featureIcon}>
							<NotebookPen size={40} color='#3182ce' />
						</div>
						<h3>Дневник здоровья</h3>
						<p>
							Ведите записи о посещениях ветеринара, прививках, питании и
							поведении вашего любимца
						</p>
					</div>

					<div className={styles.featureCard}>
						<div className={styles.featureIcon}>
							<CalendarCheck size={40} color='#3182ce' />
						</div>
						<h3>Напоминания</h3>
						<p>
							Получайте уведомления о важных событиях: прививках, обработках от
							паразитов и визитах к врачу
						</p>
					</div>

					<div className={styles.featureCard}>
						<div className={styles.featureIcon}>
							<HeartPulse size={40} color='#3182ce' />
						</div>
						<h3>Медицинская карта</h3>
						<p>
							Храните все медицинские документы, результаты анализов и
							назначения врачей в одном месте
						</p>
					</div>
				</div>
			</section>

			{/* Как это работает */}
			<section className={styles.howItWorks}>
				<h2>
					Как это работает? <span className={styles.emoji}>🤔</span>
				</h2>
				<div className={styles.steps}>
					<div className={styles.step}>
						<div className={styles.stepNumber}>1</div>
						<div className={styles.stepContent}>
							<h3>Создайте профиль питомца</h3>
							<p>Добавьте фото, породу, возраст и особенности вашего любимца</p>
						</div>
					</div>
					<div className={styles.step}>
						<div className={styles.stepNumber}>2</div>
						<div className={styles.stepContent}>
							<h3>Добавляйте записи</h3>
							<p>
								Фиксируйте важные события, посещения врача и изменения в
								состоянии
							</p>
						</div>
					</div>
					<div className={styles.step}>
						<div className={styles.stepNumber}>3</div>
						<div className={styles.stepContent}>
							<h3>Получайте напоминания</h3>
							<p>Система уведомит вас о предстоящих процедурах и визитах</p>
						</div>
					</div>
				</div>
			</section>

			{/* Отзывы */}
			<section className={styles.testimonials}>
				<h2>
					Что говорят наши пользователи?{' '}
					<span className={styles.emoji}>💬</span>
				</h2>
				<div className={styles.testimonialCards}>
					<div className={styles.testimonial}>
						<div className={styles.quote}>“</div>
						<p>
							Теперь все медицинские карты моих трёх кошек в одном месте. Больше
							не нужно искать бумажные записи!
						</p>
						<div className={styles.author}>- Анна, владелец кошек</div>
					</div>
					<div className={styles.testimonial}>
						<div className={styles.quote}>“</div>
						<p>
							Напоминания о прививках спасают! Ни разу не пропустили вакцинацию
							с тех пор как пользуемся сервисом.
						</p>
						<div className={styles.author}>- Максим, владелец собаки</div>
					</div>
				</div>
			</section>

			{/* Финальный CTA */}
			<section className={styles.finalCta}>
				<h2>
					Готовы начать заботиться о питомце проще?{' '}
					<span className={styles.emoji}>🚀</span>
				</h2>
				<Button
					type='primary'
					size='large'
					onClick={() => navigate('/auth/register')}
					className={styles.ctaButton}
				>
					Зарегистрироваться бесплатно
				</Button>
			</section>
		</div>
	)
}
