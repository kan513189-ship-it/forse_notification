# 藤田厩舎マネージャー (Android 16 / API 36 対応)

藤田晋オーナー(netkeiba馬主ID: 232031)の出走馬に絞って、予定・結果・次走報を一元管理するアプリです。

## 主な機能
- **登録馬一覧**: 追跡したい馬を登録(手動追加 or netkeibaから自動取得)
- **出走予定(ホーム画面)**: 直近の出走予定を一覧表示、「今すぐ更新」で手動同期
- **自動同期**: WorkManagerで12時間ごとにnetkeibaの藤田晋オーナーページを巡回し、馬リスト・今週の出走予定を取得
- **出走リマインダー通知**: レース24時間前になったら通知
- **レース結果の記録**: 着順・賞金・メモを保存
- **次走報・メモ登録**: 「次はこのレースを目標にしている」といった情報を自由に記録

## APKだけ欲しい場合(Android Studio不要)
このプロジェクトには GitHub Actions のビルド設定 (`.github/workflows/build-apk.yml`) を同梱しています。
Android Studioをインストールしなくても、以下の手順でAPKファイルを入手できます。

1. GitHubで新しいリポジトリ(Public/Privateどちらでも可)を作成する
2. このプロジェクト一式をそのリポジトリにpushする
   ```bash
   cd FujitaHorseApp
   git init
   git add .
   git commit -m "initial commit"
   git branch -M main
   git remote add origin https://github.com/<あなたのユーザー名>/<リポジトリ名>.git
   git push -u origin main
   ```
3. GitHubのリポジトリページ → 「Actions」タブを開く → 自動的に「Build APK」ワークフローが走り始めます
   (走らない場合は「Run workflow」ボタンで手動実行できます)
4. ビルドが完了(数分程度)したら、そのワークフロー実行結果のページ下部「Artifacts」欄にある
   `app-debug-apk` をダウンロードする(zipの中に `app-debug.apk` が入っています)
5. スマホに転送して「提供元不明のアプリ」インストールを許可すればインストールできます
   (デバッグ用APKのため、Playストア配布用の署名はされていません)

※ GitHub Actionsは無料枠でも十分に収まる範囲のビルドです。

## Android Studioで開く場合

1. Android Studio (最新版) で `FujitaHorseApp` フォルダを開く
2. 初回同期でSDK Platform 36のインストールを求められたら許可
3. 実機/エミュレータで実行

※ Gradle Wrapperのjar本体は含めていません。Android Studioで開くと自動生成されます。

## ⚠️ netkeibaスクレイピングに関する重要な注意
- netkeibaは公式APIを提供しておらず、**自動スクレイピングを明確に禁止してはいないものの**、
  短時間に大量アクセスすると通信制限(アクセス禁止)がかかる場合があります。
- このアプリはリスクを抑えるため、同期間隔を **12時間に1回** に制限しています
  (`SyncScheduler.kt` の `intervalHours` で変更可能ですが、**これより頻繁にしないことを強く推奨**します)。
- `NetkeibaScraper.kt` 内のCSSセレクタは、一般的なnetkeibaのページ構造を想定した実装例です。
  **実際のHTML構造とズレて正しく取得できない場合があります。** その場合はChrome DevTools等で
  対象ページ (`db.netkeiba.com/horse/list.html?owner_id=232031` や
  `db.netkeiba.com/owner/thisweek/232031/`) のHTML構造を確認し、セレクタを調整してください。
- あくまで個人利用目的のツールとして想定しています。netkeibaの利用規約は変更される可能性があるため、
  継続的な利用前に最新の利用規約をご確認ください。
- 自動取得がうまくいかない場合でも、アプリ内で手動登録(予定・結果・次走報)が可能なため、
  アプリ自体は問題なく使えます。

## Android 16 対応のポイント
- `compileSdk` / `targetSdk` を36に設定
- edge-to-edge表示の強制対応(`enableEdgeToEdge()`)
- 予測的戻る操作(Predictive Back)への対応
- Android 13+ (API33+) の通知権限(`POST_NOTIFICATIONS`)を起動時にリクエスト

## 構成
- `data/` : Room エンティティ・DAO・Repository (Horse, RaceEntry, RaceResult, NextRaceReport)
- `network/NetkeibaScraper.kt` : Jsoupベースのスクレイパー
- `work/` : WorkManagerによる定期同期(`SyncWorker`)とスケジューラ(`SyncScheduler`)
- `notification/NotificationHelper.kt` : 出走リマインダー通知
- `ui/` : Compose画面(ホーム/登録馬一覧/馬詳細) + ViewModel

## 今後の拡張案
- 個別馬ページからのレース結果自動取得(現状は今週の出走予定のみ自動取得)
- 通知タイミングのユーザー設定化(現在は一律24時間前)
- 複数馬主への対応(現在は藤田晋オーナー固定)
